package gg.kuken.core.security

/**
 * Represents a salted hashing algorithm.
 *
 * Unlike [Hash], this interface indicates that the hashing method to be used will use a salt of a
 * specific length ([saltLength]).
 *
 * By using salt, hash methods from this interface are generally safer.
 */
public interface SaltedHash : Hash {

    public val saltLength: Int
}
