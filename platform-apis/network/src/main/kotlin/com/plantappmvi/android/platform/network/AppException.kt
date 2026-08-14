package com.plantappmvi.android.platform.network

import java.io.IOException

/**
 * The only exception type allowed to leave this module.
 *
 * It extends [IOException] because an OkHttp `Interceptor` may only throw
 * `IOException` — which is convenient rather than a compromise: it means a
 * transport failure and a translated failure arrive at the repository through
 * the same channel, and the repository is the single place that catches.
 *
 * Nothing above `data` ever sees one of these. Repositories catch them and
 * return a sealed result case instead.
 */
sealed class AppException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause) {

    /** No usable connection: DNS failure, timeout, socket reset, airplane mode. */
    class Network(cause: Throwable? = null) : AppException("Network unavailable", cause)

    /** The server answered, but not with success. */
    class Server(val statusCode: Int) : AppException("Server returned $statusCode")

    /** The body arrived but did not match the contract. */
    class Parse(cause: Throwable? = null) : AppException("Malformed response body", cause)

    /** Anything the interceptor could not classify. */
    class Unknown(cause: Throwable? = null) : AppException("Unknown network failure", cause)
}
