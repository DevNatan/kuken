package gg.kuken.core.security

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import java.security.SecureRandom
import java.util.Random

private const val SALT_LENGTH = 16
private const val HASH_COST = 12

/**
 * Bcrypt salted hash implementation.
 */
internal object BcryptHash : SaltedHash {

    override val name: String = "Bcrypt"
    override val saltLength: Int = SALT_LENGTH
    private val random: Random = SecureRandom()

    private fun generateSalt(): ByteArray = ByteArray(saltLength).also { bytes ->
        random.nextBytes(bytes)
    }

    override fun hash(value: CharArray): String = OpenBSDBCrypt.generate(value, generateSalt(), HASH_COST)

    override fun compare(value: CharArray, hash: String): Boolean = OpenBSDBCrypt.checkPassword(hash, value)
}
