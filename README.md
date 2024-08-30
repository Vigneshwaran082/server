**Server Purpose**

This **light-weight** server acts as a fallback solution for the front-end team, providing predefined responses when the back-end server is unavailable or down.

**How It Works**

The server reads configurations from a server.properties file, where each endpoint is mapped to a corresponding JSON file located in the **JSON_Files** directory. To set up the server, simply place any number of JSON files in the **JSON_Files** directory and define the endpoint mappings in the server.properties file. When a request is made to a specified endpoint, the server responds with the content of the associated JSON file, allowing the front-end team to continue development and testing without dependency on the back-end services.