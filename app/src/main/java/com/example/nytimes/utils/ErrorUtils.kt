package com.example.nytimes.utils

import androidx.annotation.VisibleForTesting
import com.example.nytimes.model.Error
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

object ErrorUtils {
    @VisibleForTesting
    const val DEFAULT_ERROR_MESSAGE = "Something went wrong! Please try again."

    @VisibleForTesting
    const val NETWORK_ERROR_MESSAGE = "No Internet Connection!"

    fun getErrorMessage(throwable: Throwable): String {
        if (throwable is IOException) return NETWORK_ERROR_MESSAGE
        if (throwable !is HttpException) return DEFAULT_ERROR_MESSAGE
        return throwable.response()?.errorBody()?.let {
            extractError(it)
        } ?: DEFAULT_ERROR_MESSAGE
    }

    fun getErrorMessage(response: Response<*>): String {
        return response.errorBody()?.let {
            extractError(it)
        } ?: DEFAULT_ERROR_MESSAGE
    }

    /**
     * Try to extract an error message from an [Error] object encoded as a JSON from [errorBody].
     * If an error occurs when trying to extract the message, returns [DEFAULT_ERROR_MESSAGE]
     */
    private fun extractError(errorBody: ResponseBody): String {
        return try {
            parseErrorMessage(errorBody.string())
        } catch (e: Exception) {
            Timber.d(e)
            DEFAULT_ERROR_MESSAGE
        }
    }

    /**
     * Try to parse an [Error] JSON encoded object and extract the message that it contains.
     * If an error occurs during the parsing, [DEFAULT_ERROR_MESSAGE] is returned.
     */
    private fun parseErrorMessage(errorJsonResponse: String): String {
        try {
            val errorResponse = Gson().fromJson(
                errorJsonResponse, Error::class.java
            )
            if (errorResponse?.fault?.faultstring != null) {
                if (errorResponse.fault?.faultstring!!.contains(
                        "java",
                        true
                    ) || errorResponse.fault?.faultstring!!.contains(
                        "Exception",
                        true
                    )
                )
                    return DEFAULT_ERROR_MESSAGE

                return errorResponse.fault?.faultstring!!
            }
        } catch (e: Exception) {
            Timber.d(e)
        }

        return DEFAULT_ERROR_MESSAGE
    }

    fun isInvalidTokenResponse(throwable: Throwable): Boolean {
        if (throwable is HttpException) {
            if (isInvalidTokenResponse(throwable.code())) {
                return true
            }
        }
        return false
    }

    fun isInvalidTokenResponse(code: Int): Boolean {
        return code == 401 || code == 403 || code == 406
    }
}