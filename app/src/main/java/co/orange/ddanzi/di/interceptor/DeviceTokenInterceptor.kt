package co.orange.ddanzi.di.interceptor

import co.orange.data.local.UserSharedPref
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class DeviceTokenInterceptor
    @Inject
    constructor(
        private val sharedPref: UserSharedPref,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest =
                originalRequest.newBuilder()
                    .header(DEVICE_TOKEN, sharedPref.deviceToken)
                    .build()
            return chain.proceed(newRequest)
        }

        companion object {
            private const val DEVICE_TOKEN = "devicetoken"
        }
    }
