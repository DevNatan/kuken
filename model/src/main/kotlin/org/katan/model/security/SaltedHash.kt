package org.katan.model.security

/**
 * Represents a salted hashing algorithm.
 *
 * Unlike [Hash], this interface indicates that the hashing method to be used will use a salt of a
 * specific length ([saltLength]).
 *
 * By using salt, hash methods from this interface are generally safer.
 */
public interface SaltedHash : Hash {

    /**
     * Returns the length of the salt that will be used in the hashing process for that algorithm.
     */
    public val saltLength: Int
}
