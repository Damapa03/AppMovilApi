package com.example.appmovilapi.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class SimpleRSAKeyProvider(private val publicKey: RSAPublicKey): RSAKeyProvider{
    override fun getPublicKeyById(keyId: String?): RSAPublicKey = publicKey
    override fun getPrivateKey(): RSAPrivateKey? = null
    override fun getPrivateKeyId(): String? = null
}

fun decodeJwt(token: String, publicKeyPEM: String): DecodedJWT? {
    return try {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----","")
            .replace("-----END PUBLIC KEY-----", "").replace("\n", ""))
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey

        val algorithm = Algorithm.RSA256(SimpleRSAKeyProvider(publicKey))

        JWT.require(algorithm).build().verify(token)
    } catch (e: Exception) {
        println("Error decoding JWT: ${e.message}")
        null
    }
}

fun getRolesFromJwt(decodedJWT: DecodedJWT, claimName: String = "roles"): List<String>{
    return try {
        val claim = decodedJWT.getClaim(claimName)
        if(!claim.isMissing && !claim.isNull){
            if (claim.asList(String::class.java) != null){
                claim.asList(String::class.java)
            } else {
                listOf(claim.asString())
            }
        } else {
            emptyList()
        }
    }catch (e:Exception){
        println("Error extrayendo roles: ${e.message}")
        emptyList()
    }
}
