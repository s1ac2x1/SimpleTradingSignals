package com.kishlaly.ta.openai.flow

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

fun postWithRetry(
    client: OkHttpClient,
    request: Request,
    retries: Int = 3
): String? {
    var response: Response? = null
    var attempts = 0

    while (response == null && attempts < retries) {
        try {
            response = client.newCall(request).execute()
        } catch (e: IOException) {
            attempts++
            println("Request failed. Retrying in 10 seconds")
            Thread.sleep(10 * 1000)
        }
    }

    return response?.body?.string()
}