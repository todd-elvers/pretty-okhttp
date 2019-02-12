pretty-okhttp
---------------------------------

Combines the powers of [OkHttp](http://square.github.io/okhttp/), 
[Java 8 interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html),
and Google's [Gson](https://github.com/google/gson) to create a thread-safe,  
easy-to-use, and easy-to-test Java 8 interface for handling HTTP requests.


<br/>

## Why not just use the libraries directly?

**tl;dr**
* Convenience methods were added for common operations (e.g. normal GET/POST, POSTing a form, etc.)
* Serialization/deserialization support was added for `Date`, `LocalDate`, and `LocalDateTime` classes
    * Multiple formats are attempted deserialization:
        * [Unix Epoch](https://en.wikipedia.org/wiki/Unix_time)
        * [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601)
        * MM/dd/yyyy
        * MM-dd-yyyy


The goal was to create an easier to use version of OkHttp that yielded in prettier code.

### Full Explanation

#### OkHttp

[OkHttp](http://square.github.io/okhttp/) is a great, thread-safe library for handling HTTP requests, 
but it requires a lot of boilerplate

##### Making it better:
Implement OkHttp behind a Java 8 interface, which simultaneously requires 
no configuration while also allowing for a high degree of customization.


#### Gson

[Gson](https://github.com/google/gson) is a great library for object serialization to/from JSON, 
but does not support date/time serialization out-of-the-box.

##### Making it better:
Serialization was added for Java's `Date`, `LocalDate`, and `LocalDateTime` classes to the Gson instance
that is available after implementing `HttpRequestHandling` (or just simply `JsonMarshalling`). 

###### Serialization:
During serialization the above date/time classes are sent over the wire as 
[ISO-8601](https://en.wikipedia.org/wiki/ISO_8601) strings. See the classes in 
[this package](https://github.com/todd-elvers/pretty-okhttp/tree/master/src/main/java/te/http/handling/serialization) 
for more details. To override this behavior entirely override the `getJsonMarshaller()`
method and initialize Gson in whatever manner you'd like.

###### Deserialization:
During de-serialization Gson will try the Unix epoch, the ISO-8601 format, and 
then a few other common formats (e.g. "MM/dd/yyyy", etc.).  See the classes in 
[this package](https://github.com/todd-elvers/pretty-okhttp/tree/master/src/main/java/te/http/handling/deserialization) 
for more details. To override this behavior entirely override the `getJsonMarshaller()`
method and initialize Gson in whatever manner you'd like.

<br/>

## How to use it

1. Make your class implement `HttpRequestHandling`
    * Override any of the default behavior if necessary (e.g. `getDefaultHeader()`, `isNon200ResponseExceptional()`, etc.) 
2. Make a request to your desired endpoint 
    * For GET requests = `executeGET`
    * For POST requests = `executePOST`, or `executeFormPOST`
3. You're done! Optionally you could marshall the resulting JSON into an object or list of objects
    * Marshalling JSON = `toJson`, `fromJson`, or `fromJsonList`
    
##### Need more customization?
If the above does not offer the customization you require you can build a `Request` object with one of the 
following convenience methods:
    
* `buildRequestForGET`
* `buildRequestForPOST`

This will return you a `Request` object you can then pass to `executeRequest()`.  If you'd like to customize 
the request further simply call `newBuilder()` on the resulting `Request` and customize to your hearts content.

And lastly, if the above is not helpful, you can always build your `Request` from scratch via `new Request.Builder()`.

<br/>

## Examples

I'm a big fan of functional programming and have come to love the functional 
Try from [vavr](http://www.vavr.io/), so if you prefer [to see examples of pretty-okhttp
using Vavr click here](docs/vavr-examples.md).  

<br/>

#### Performing a GET to a URL and passing along the status code:

```java
class WebsiteHealthChecker implements HttpRequestHandling {
    public Optional<Integer> fetchStatusCodeFromService(String url) {
        try {
            return Optional.of(
                    executeFormPOST(url, formData).getStatusCode()
            );
        } catch(IOException ex) {
            // Do nothing
        }
        
        return Optional.empty();
    }
}
```

And an associated, yet completely bogus, Spock test illustrating how testing works:

```groovy
class WebsiteHealthCheckerTest extends Specification {
    
    void "returns empty optional when service returns non-200"() {
        given: 'an implementation that always throws HttpClientException'
            def websiteChecker = new WebsiteHealthChecker() {
                @Override
                HttpResponse executeGET(String url) {
                    throw new HttpClientException("failed, non-200")
                }   
            }
        
        when: 'we fetch the status code for some service URL'
            Optional<Integer> result = websiteChecker.fetchStatusCodeFromService("some-url")
        
        then: 'the exception was swallowed and we received an empty optional'
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
    
    private Integer uploadFormWithRetry(String url, Map<String, ?> formData, int retryCount) {
        try {
            return executeFormPOST(url, formData).getStatusCode();
        } catch(HttpClientException ex) {
            // 400 error - we don't normally want to retry on these
        } catch(NoResponseException | HttpServerException ex) {
            // 500 error or no response - perhaps retrying the request will work
            handleRetry();
        }
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
    
    static class User {
        private String username;
        
        // Map "website" in JSON to this field
        @SerializedName("website")  
        private  String url;
        
        // Getters/Setters etc.
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
    * Add `compile 'com.github.todd-elvers:pretty-okhttp:4.0.0'`
