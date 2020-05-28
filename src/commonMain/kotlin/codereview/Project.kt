package codereview

import io.ktor.util.InternalAPI
import io.ktor.util.encodeBase64
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val localPath: String,
    val providerPath: String,
    val name: String
) {
    @InternalAPI
    val id: String = localPath.encodeBase64()
}