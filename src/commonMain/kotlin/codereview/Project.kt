package codereview

import kotlinx.serialization.Serializable


@Serializable
data class Project(
    val localPath: String,
    val providerPath: String,
    val name: String
) {
    /** TODO : Get encodeToBase64 to work */
//    val id = localPath.encodeToBase64()
    val id
    get() = localPath.replace("/", "")
}
