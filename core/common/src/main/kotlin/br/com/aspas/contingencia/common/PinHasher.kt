package br.com.aspas.contingencia.common
import java.security.MessageDigest
import java.security.SecureRandom
object PinHasher {
fun newSalt(): String {
val bytes = ByteArray(16); SecureRandom().nextBytes(bytes)
return bytes.joinToString("") { "%02x".format(it) }
}
fun hash(pin: String, salt: String): String {
val md = MessageDigest.getInstance("SHA-256")
md.update(salt.toByteArray())
return md.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
}
fun verify(pin: String, salt: String, expected: String): Boolean = hash(pin, salt) == expected
}
