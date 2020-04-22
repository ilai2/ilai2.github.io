package guizilla.sol.server

/**
  * A class for common HTTP headers and HTML pages with informative messages
  */
class Error {
  /**
    * HTTP header for 404s
    */
  val h404: String = "HTTP/1.0 404 Not Found\n"

  /**
    * HTTP header for 500s
    */
  val h500: String = "HTTP/1.0 500 Internal Server Error\n"

  /**
    * HTTP header for 400s
    */
  val h400: String = "HTTP/1.0 400 Bad Request\n"

  /**
    * HTTP header for 405s
    */
  val h405: String = "HTTP/1.0 405 Method Not Allowed\n"

  /**
    * HTTP header for 505s
    * Occurs when there's stuff on the first line after POST/GET that isn't valid
    */
  val h505: String = "HTTP/1.0 505 HTTP Version not supported\n"

  /**
    * An HTML page for incorrect number of arguments on first line/incorrect
    * HTTP version
    */
  val http: String =
    "<html><body><p>Server error: Bad request - HTTP Version Not Supported" +
      "</p></body></html>"

  /**
    * An HTML page for a POST request lacking a content-length header with a
    * number immediately following it
    */
  val contentLength: String =
    "<html><body><p>Server error: Bad request - Content length error" +
      "</p></body></html>"

  /**
    * An HTML page for a POST request lacking form data or other form data
    * errors
    */
  val formData: String =
    "<html><body><p>Server error: Bad request - Form data error" +
      "</p></body></html>"

  /**
    * An HTML page for an invalid class name
    */
  val classNotFound: String =
    "<html><body><p>Server error: Invalid class name in URL - ClassNotFoundException" +
      "</p></body></html>"

  /**
    * An HTML page for an invalid method name
    */
  val methodNotFound: String =
    "<html><body><p>Server error: Invalid method name in URL - MethodNotFoundException" +
      "</p></body></html>"

  /**
    * An HTML page for a request that does not start with either GET or POST
    */
  val notGetPost: String =
    "<html><body><p>Server error: Bad request - Method not GET or POST" +
      "</p></body></html>"

  /**
    * An HTML page for attempting to access an ID that doesn't exist
    */
  val invalidID: String =
    "<html><body><p>Server error: Invalid ID in URL" +
      "</p></body></html>"

  /**
    * An HTML page for illegal access exceptions
    */
  val illegalAccess: String =
    "<html><body><p>Server error: Illegal Access Exception" +
      "</p></body></html>"

  /**
    * An HTML page for instantiation exceptions
    */
  val instantiation: String =
    "<html><body><p>Server error: Instantiation Exception" +
      "</p></body></html>"

  /**
    * An HTML page for an exception in the initializer
    */
  val initializer: String =
    "<html><body><p>Server error: Exception in Initializer Error" +
      "</p></body></html>"

  /**
    * An HTML page for security exceptions
    */
  val security: String =
    "<html><body><p>Server error: Security Exception" +
      "</p></body></html>"

  /**
    * An HTML page for class cast exceptions
    */
  val classCast: String =
    "<html><body><p>Server error: Class Cast Exception" +
      "</p></body></html>"
}