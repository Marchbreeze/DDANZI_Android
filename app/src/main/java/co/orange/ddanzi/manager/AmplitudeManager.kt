package co.orange.ddanzi.manager

object AmplitudeManager {
    private lateinit var amplitude: Amplitude

    fun init(
        context: Context,
        apiKey: String,
    ) {
        amplitude =
            Amplitude(
                Configuration(
                    apiKey = apiKey,
                    context = context.applicationContext,
                ),
            )
    }

    fun trackEvent(
        eventName: String,
        properties1: Map<String, Any>? = null,
        properties2: Map<String, Any>? = null,
    ) {
        when {
            properties1 == null && properties2 == null -> {
                amplitude.track(eventName)
            }

            properties1 != null && properties2 == null -> {
                amplitude.track(eventName, properties1)
            }

            properties1 != null && properties2 != null -> {
                val combinedProperties = mutableMapOf<String, Any?>()
                combinedProperties.putAll(properties1)
                combinedProperties.putAll(properties2)
                amplitude.track(eventName, combinedProperties)
            }
        }
    }

    fun updateProperties(
        propertyName: String,
        values: String,
    ) {
        amplitude.identify(Identify().set(propertyName, values))
    }

    fun updateIntProperties(
        propertyName: String,
        intValues: Int,
    ) {
        amplitude.identify(Identify().set(propertyName, intValues))
    }

    fun plusIntProperties(propertyName: String) {
        amplitude.identify(Identify().add(propertyName, 1))
    }
}
