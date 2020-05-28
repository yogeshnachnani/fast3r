package codereview

/**
 * Basically a [Changeset] is _something_ that needs review.
 * Different providers call it differently. For eg, it is a PullRequest in github or a Diff in Phabricator
 */
interface Changeset {
    /** Unique identifier as given by the provider */
    fun id()
    fun title()
    fun description()
}


