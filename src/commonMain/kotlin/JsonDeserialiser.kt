import kotlinx.serialization.json.Json

val jsonParser = Json {
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
}

