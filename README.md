##Analytics Demo project setup

1. Install JDK 8, Maven 3 and then checkout the project to your local machine.
2. Download and install Apache Spark and Apache Cassandra latest versions
3. Start Spark and Cassandra Server
4. Go to project root and run `mvn clean install`
5. Maven will run the integration tests and create a file named analytics_demo-1.0-SNAPSHOT.jar in {PROJECT_ROOT}/target folder
6. Javadocs will be generated in {PROJECT_ROOT}/target/site folder
7. Run the service with command `java -jar analytics_demo-1.0-SNAPSHOT.jar`
8. This will start the Spring Boot application with embedded Jetty server

###Usage information
####Pushing data 
*Use any REST client or Google Chrome extension like Postman or CURL to post data to the service*

**URL:** http://localhost:9000/analytics/pageview

**HTTP Method:** PUT

**Headers:**

- Accept = application/json, text/plain, */*

- Content-Type = application/json

**Data:**
```
{
  "userId" : "username@domain.com",
  "pageId" : "homepage"
}
```

####Retrieving Data

**URL:** http://localhost:9000/analytics/{userId}/pageviews?limit={N}

- userId = User ID used to insert pageview data

- N = number of records to fetch

**HTTP Method:** GET

**Headers:**

- Accept = application/json, text/plain, */*

- Content-Type = application/json

**Sample Response**

```
{
    "userId": "ramsankar83@gmail.com",
    "pageViews": [
        {
            "pageId": "homepage",
            "viewedAt": "2015-03-08T08:39:57+0000"
        },
        {
            "pageId":  "homepage",
            "viewedAt": "2015-03-08T05:00:15+0000"
        },
        {
            "pageId":  "page5",
            "viewedAt": "2015-03-08T04:59:46+0000"
        },
        {
            "pageId":  "homepage",
            "viewedAt": "2015-03-08T04:59:08+0000"
        },
        {
            "pageId":  "homepage",
            "viewedAt": "2015-03-08T04:57:20+0000"
        },
        {
            "pageId":  "page1",
            "viewedAt": "2015-03-08T04:52:31+0000"
        },
        {
            "pageId":  "homepage",
            "viewedAt": "2015-03-07T16:38:32+0000"
        },
        {
            "pageId":  "page2",
            "viewedAt": "2015-03-07T16:08:23+0000"
        },
        {
            "pageId":  "homepage",
            "viewedAt": "2015-03-07T08:58:45+0000"
        },
        {
            "pageId": "offers page",
            "viewedAt": "2015-03-07T08:33:44+0000"
        }
    ]
}
```

###Additional Information
- Authentication using API key is not implemented

- By default there will be a 10 second delay between data insertion and appearance in the result

####System properties to affect application behavior

- *spring.boot.container.port* = Port in which the REST API service hosted by Spring boot has to run, default is 9000

- *spark.cassandra.connection.host* = Cassandra host name for Apache spark MapReduce framework to interact with, default is localhost

- *spark.ctx.app.name* = Application name for Apache Spark Context, default is Analytics Demo

- *spark.ctx.master* = Master URL to which Apache Spark has to connect to, default local[2]. <br> 2 is the number of worker threads and is the minimum value and ideally same as number of cores in the machine <br> refer https://spark.apache.org/docs/0.8.1/scala-programming-guide.html#master-urls

- *spark.stream.interval* = Frequency at which as Spark Streaming framework has to ingest or process streamed input data in seconds, default 10. This will reflect in the application latency as well

- *in.memory.q.size* = Streaming is implemented to use an in-memory BlockingQueue, this parameter specifies the maximum size of the queue. Default is 1000
