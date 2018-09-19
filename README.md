
# Simple URL Shortener in Scala and Play Framework

This is a simple __URL Shortener__ written in Scala and using the Play Framework. It's similar to public services like [TinyURL](https://tinyurl.com/) or [Google URL Shortener](https://goo.gl/).

It implements a _POST endpoint_ to get a short URL and a _redirecting service_ to go to the original URLs when accessing the short URLs.

The endpoint can be called with a POST call to path `/api/shorten` with default domain `http://localhost:9000`.

The short URL will be a short base 62 code added to the URL  `http://localhost:9000/go/`. This URL can be configured in the `application.conf` file to be a different domain like `https://mycompa.ny/`.

## Requirements and running

You need a Redis installation working somewhere. In the file `application.conf` you can configure the host and port, which by default are `localhost` and `6379` respectively.

You need to install on your machine the [Scala interactive build tool](https://www.scala-sbt.org/) `sbt`.

Running the application is as easy as executing the command `sbt run`.

Deploying is very easy also. With Play Framework a Java application is generated using the `dist`  command. This application includes a stand-alone HTTP server so only a Java installation is needed on the production server.

## API REST example

__Request__:
* Method: POST 
* URL: http://localhost:9000/api/shorten
* Content-Type: application/json
* Body: `{"originalUrl": "https://www.nasa.gov"}`

__Response__:
* HTTP/1.1 201 Created
* Content-Type: application/json
* Body:
```
{
  "originalUrl": "https://www.nasa.gov",
  "shortUrl": "http://localhost:9000/go/K4",
  "creationDate": "2018-07-06T16:12:14.878+0000"
}
```

## Design decisions

__Short URL generation__

A unique short URL should be generated for each original long URL. There are basically three main ways to do that:

1. Hashing the long URL and converting it to a base 62 alphabet. The problem here is that is difficult to get a short resulting string, so we should take a substring of the result and deal with possible collisions.
2. Generate available short URLs beforehand. There can be problems with concurrency if not well designed.
3. Use a global unique identifier. This identifier is an incrementing integer value that is converted to a base 62 code. To prevent easily guessable codes we use a _salt_ to shuffle the base 62 alphabet before conversion.

I've chosen for this example the third solution. It's easy to implement with a NoSQL database providing an atomic _increment_ command. Furthermore, it can be easily scaled up if the number of the database shard / slot / partition / key range is used together with this incrementing identifier.

This third solution gives us also this characteristic: every time we try to convert the same long URL we get a different short URL. This feature, provided also by __Google URL Shortener__ can be a desirable feature in these cases:

* We have multiple users and each user cannot see the same short URL, even for the same original URL.
* We want to track accesses to the same long URL from different places, where each place has a different short URL.

__Database__

The data to store and to retrieve is very simple so an NoSQL database can give us a very good performance, taking into account that it can also be easily scalable. A solution like [Apache Cassandra](http://cassandra.apache.org/) or [Redis](https://redis.io/) would fit. For this project I've chosen Redis basically because I already have a local installation.

Furthermore, with these NoSQL databases, is very straightforward to implement an expiring short URL by making use of the TTL property of stored keys.

Data is stored this way:

* Global unique id is an integer value with key `url-id:id`
* Each generated short URL has a key like `url:`+`unique id` and it stores a json value like: `{"originalUrl":"https://www.nasa.gov","shortUrl":"http://localhost:9000/go/K4","creationDate":"2018-07-06T16:12:14.878+0000"}`
* Counter for each access to a short URL with key `url-counter:`+`unique id`

__Other__

Usually, the second time you access an URL on your browser some content is taken directly from the cache. Similarly, if the server says on the first access that a site is MOVED PERMANENTLY to another address (short to long URL conversion), the new address is cached. But, what happens if we want to count every access to the short URL? In this case we have to add some values to the "Cache-Control" header of the redirection response. These are: `no-cache, no-store, max-age=0, must-revalidate`

## TODO

__Additional features__
* Add a web form to easily request the short version of an input long URL
* Add an API method to get the counter value of a short URL, which is already being stored on Redis.
* Give the chance to create a custom short URL
* Make the short URL to expire after a specific period
* Support user authentication

__Technical improvements__
* Use Futures for extended asynchronous operation
* Adding testing with Gatling 
