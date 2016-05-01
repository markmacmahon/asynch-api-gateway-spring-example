# Asynchronous API Gateway Example

An API gateway (Spring Boot) that proxies two different REST end points and outputs the results into a merged JSON response.

The following 2 endpoints are aggregated:

   * Fetch user's data: [http://jsonplaceholder.typicode.com/users/1](http://jsonplaceholder.typicode.com/users/1)

   * Fetch all posts by user:  [http://jsonplaceholder.typicode.com/posts?userId=1](http://jsonplaceholder.typicode.com/posts?userId=1)
   

The result is the same user Json but with an additional field that contains the list of posts.
        
Implementation is based on Spring Boot's non-blocking IO with Google's Guava ListenableFuture preferred over the Spring equivalent due to the better library support.

Note: 

   1. If one external API request fails then the entire API request fails. Partial responses or fallbacks are not implemented.
   2. If there is an error with the [downstream api](http://jsonplaceholder.typicode.com) then that error will be propagated. This may occur when the ApiControllerIntegrationTest is run or when the API is being tested over curl.
   3. http timeout for external api requests is set to 10 seconds (see AppMain). 
   4. This app is for demonstration purposes only so a local thread pool is used instead of something more robust like a messaging system.


## Prequisites

   * Java 8
   * Maven 3.x
   
```
mvn clean install
```   


## Running the Tests

```
mvn test
```

## Running the Example

```
mvn spring-boot:run
```

Fetch User 1 as follows:
```
curl http://localhost:8080/users/1
```



