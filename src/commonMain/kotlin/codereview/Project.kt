package codereview

import encodeToBase64
import kotlinx.serialization.Serializable


@Serializable
data class Project(
    val localPath: String,
    val providerPath: String,
    val name: String
) {
    val id = localPath.encodeToBase64()
}
