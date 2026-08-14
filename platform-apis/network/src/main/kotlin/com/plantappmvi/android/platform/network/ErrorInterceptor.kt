package com.plantappmvi.android.platform.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Status-code validation and error translation, in one place, so no repository
 * repeats it and no repository has to know what an HTTP code is.
 */
class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = try {
            chain.proceed(chain.request())
        } catch (cause: SocketTimeoutException) {
            throw AppException.Network(cause)
        } catch (cause: UnknownHostException) {
            throw AppException.Network(cause)
        } catch (cause: AppException) {
            throw cause
        } catch (cause: IOException) {
            throw AppException.Network(cause)
        }

        if (!response.isSuccessful) {
            val statusCode = response.code
            response.close()
            throw AppException.Server(statusCode)
        }

        return response
    }
}
