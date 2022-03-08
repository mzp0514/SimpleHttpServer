# Simple Web Server

This is a multi-threaded file-based web server with thread-pooling implemented in Java, with the HTTP/1.1 keep-alive behavior implemented.

The webserver is a static file system that can allow client to have access to the static html files on the server. 

## Project structure

    .
    ├── WebContent                          # Web root directory containing files for test
    |   ├── test.html                              
    │   └── index.html              
    ├── src                                 # Source files
    │   ├── main             
    |   |   ├── java
    |   |   |   └── com.webserve 
    |   |   |       ├── config             
    |   |   |       |   ├── Configuration             
    |   |   |       |   └── ConfigurationManager              
    |   |   |       ├── core              
    |   |   |       |   ├── ClientHandler              
    |   |   |       |   └── WebServer              
    |   |   |       ├── exceptions              
    |   |   |       |   ├── HttpConfigurationException              
    |   |   |       |   ├── HttpExecutionException              
    |   |   |       |   └── HttpParsingException             
    |   |   |       └── http              
    |   |   |           ├── HttpMethod             
    |   |   |           ├── HttpParser              
    |   |   |           ├── HttpRequest             
    |   |   |           ├── HttpResponse             
    |   |   |           └── HttpStatusCode             
    |   |   └── resources           
    |   |       └── log4j2.properties              
    │   └── test
    |       └── com.webserver.http
    |           └── HttpParserTest
    ├── webserver.properties            # Webserver configuration file
    ├── pom.xml                         # Maven configuration file
    └── README.md

## Usage

```
maven package
```

```
java -jar WebServer-1.0-SNAPSHOT.jar [--conf] [Webserver.properties]
# The default configuration file is Webserver.properties
```

## Functions

### Configuration

```
# Webserver.properties
port=8080            # The port of the server.
max_thread_num=100   # The maximum thread number of the thread pool to handle the clients.
webroot=WebContent   # Directory where the files that clients can request are stored.
timeout=5            # The time in seconds that the host will allow an idle connection to remain open before it is closed.
```

### Request methods

According to RFC-2616,  the methods GET and HEAD MUST be supported by all general-purpose servers and all other methods are OPTIONAL. This project currently only supports GET and HEAD.

#### Http request format

```
     HTTP-message   = method SP request-target SP HTTP-version CRLF
                      *( header-field CRLF )
                      CRLF
                      [ message-body ]
```

The `request-target` part specifies the path of the file to access.

The `query` part (`?[key1]=[value1]&[key2]=[value2]`) in `request-target` and `message-body` are supported but the parsed results are simply ignored.

#### Example request

```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="98", "Google Chrome";v="98"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
cp-extension-installed: Yes
Sec-Fetch-Site: cross-site
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.9
```

#### Example response

```
HTTP/1.1 200 OK
Keep-Alive:timeout=5
Connection:keep-alive
Content-Length:143
Content-Type:text/html

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>index</title>
</head>
<body>
hello world
</body>
</html>
```

### Status codes

```
SC_OK(200, "OK"),
SC_BAD_REQUEST(400, "Bad Request"),
SC_METHOD_NOT_ALLOWED(401, "Method Not Allowed"),
SC_NOT_FOUND(404, "Not Found"),

SC_INTERNAL_SERVER_ERROE(500, "Internal Server Error"),
SC_NOT_IMPLEMENTED(501, "Not Implemented");
```

### Keep-alive behavior

If the client specifies `Connection: keep-alive` in the request message,

- If the request is processed successfully (status code 200), the connection will be kept alive for another `timeout` (specified in the configuration file) seconds. 
- If the request is processed unsuccessfully, the connection will be closed. 
- If the connection is idle for more than `timeout` seconds, the connection will be closed.

If the keep-alive behavior is triggered, the response message will contain `Keep-Alive:timeout=[timeout] Connection:keep-alive`.

