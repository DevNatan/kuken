package gg.kuken.core.security

/**
 * An unsalted hashing algorithm.
 *
 * It is used to apply hashing functions to input and also to  [compare] values that have already
 * been applied to hashing functions. See [SaltedHash] for salted hashes.
 */
public interface Hash {

    public companion object {

        public val SHA1: Hash = SHA1Hash
        public val Bcrypt: Hash = BcryptHash
    }

    public val name: String

    /**
     * Creates a hash for the specific input ([value]), that can be compared later using the
     * [compare] method.
     *
     * @param value The input.
     * @return The input with the hashing function applied.
     */
    public fun hash(value: CharArray): String

    /**
     * Checks whether the [value] is valid for the specified [hash].
     *
     * @param value The input.
     * @param hash The hash (obtained through [Hash.hash])
     */
    public fun compare(value: CharArray, hash: String): Boolean
}
