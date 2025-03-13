package gg.kuken.core.security

import org.bouncycastle.jcajce.provider.digest.SHA1

internal object SHA1Hash : Hash {

    override val name: String = "SHA1"
    private val digest = SHA1.Digest()

    override fun hash(value: CharArray): String =
        value.map { char -> char.code.toByte() }.toByteArray().let(digest::digest).decodeToString()

    override fun compare(value: CharArray, hash: String): Boolean =
        error("$name hash comparison is not supported")
}
