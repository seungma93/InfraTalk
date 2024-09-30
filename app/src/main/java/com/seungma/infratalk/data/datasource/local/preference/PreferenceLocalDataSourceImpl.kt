package com.seungma.infratalk.data.datasource.local.preference

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.seungma.infratalk.data.model.request.preference.UserTokenSetRequest
import com.seungma.infratalk.data.model.response.preference.SavedEmailResponse
import com.seungma.infratalk.data.model.response.preference.UserTokenResponse
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class PreferenceLocalDataSourceImpl(private val context: Context) : PreferenceDataSource {
    private val KEY_ALIAS = "MyKeyAlias"
    private val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private val CHARSET = "UTF-8"
    private val KEY = "KEY"
    private val EMAIL_KEY = "EMAIL_KEY"

    override fun getUserToken(): UserTokenResponse {
        return kotlin.runCatching {

            val sharedPreferences = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE)
            sharedPreferences.getString("${KEY}_iv", null)?.let {
                val encryptedData = Base64.decode(sharedPreferences.getString("${KEY}_data", ""), Base64.DEFAULT)
                val iv = Base64.decode(sharedPreferences.getString("${KEY}_iv", ""), Base64.DEFAULT)

                val cipher = getCipher(Cipher.DECRYPT_MODE, iv)
                val decryptedData = cipher.doFinal(encryptedData)
                UserTokenResponse(
                    token = String(decryptedData, charset(CHARSET))
                )
            } ?: run {
                UserTokenResponse(
                    token = null
                )
            }
        }.onFailure {

        }.getOrThrow()
    }

    override fun setUserToken(userTokenSetRequest: UserTokenSetRequest) {
        kotlin.runCatching {
            val cipher = getCipher(Cipher.ENCRYPT_MODE)
            val encryptedData = cipher.doFinal(userTokenSetRequest.token.toByteArray(charset(CHARSET)))
            val iv = cipher.iv

            val sharedPreferences = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("${KEY}_data", Base64.encodeToString(encryptedData, Base64.DEFAULT))
            editor.putString("${KEY}_iv", Base64.encodeToString(iv, Base64.DEFAULT))
            Log.d("seungma", "PreferenceLocalDataSourceImpl/setUserToken: 수행완료" )
            editor.apply()
        }.onFailure {
            Log.d("seungma", "PreferenceLocalDataSourceImpl/setUserToken: " + it.message)
        }
    }

    override fun deleteUserToken() {
        kotlin.runCatching {
            val sharedPreferences = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // 데이터 삭제
            editor.remove("${KEY}_data")
            editor.remove("${KEY}_iv")

            // 변경사항을 저장합니다
            editor.apply()

            Log.d("seungma", "PreferenceLocalDataSourceImpl/deleteUserToken: 수행완료")
        }.onFailure {
            Log.d("seungma", "PreferenceLocalDataSourceImpl/deleteUserToken: " + it.message)
        }
    }

    override fun getSavedEmail(): SavedEmailResponse {
        return kotlin.runCatching {

            val sharedPreferences = context.getSharedPreferences("UserPreference", Context.MODE_PRIVATE)
            sharedPreferences.getString("${EMAIL_KEY}_iv", null)?.let {
                val encryptedData = Base64.decode(sharedPreferences.getString("${EMAIL_KEY}_data", ""), Base64.DEFAULT)
                val iv = Base64.decode(sharedPreferences.getString("${EMAIL_KEY}_iv", ""), Base64.DEFAULT)

                val cipher = getCipher(Cipher.DECRYPT_MODE, iv)
                val decryptedData = cipher.doFinal(encryptedData)
                SavedEmailResponse(
                    email = String(decryptedData, charset(CHARSET))
                )
            } ?: run {
                SavedEmailResponse(
                    email = null
                )
            }
        }.onFailure {

        }.getOrThrow()
    }

    private fun getCipher(mode: Int, iv: ByteArray? = null): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = getKey()

        when (mode) {
            Cipher.ENCRYPT_MODE -> {
                cipher.init(Cipher.ENCRYPT_MODE, key)
            }
            Cipher.DECRYPT_MODE -> {
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            }
        }
        return cipher
    }

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }


}





