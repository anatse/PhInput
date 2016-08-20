package org.asem.spray.security

import java.io.FileInputStream
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher

import com.typesafe.config.ConfigFactory
import spray.caching._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
/**
  * Created by gosha-user on 30.07.2016.
  */
object RSA {
  private val config = ConfigFactory.load()
  lazy val keyStore:KeyStore = {
    val ks = KeyStore.getInstance("jks")
    ks.load(new FileInputStream (keyStorePath), password.toCharArray)
    ks
  }

  private val keyStorePath = config.getString("secureKey.path")
  private val password = config.getString("secureKey.password")
  private val alias = config.getString("secureKey.alias")
  private val keyPwd = new KeyStore.PasswordProtection (config.getString("secureKey.keyPassword").toCharArray)

  val getPassword = {
    new KeyStore.PasswordProtection (password.toCharArray)
  }

  private lazy val pkEntry = {
    keyStore.getEntry(alias, keyPwd).asInstanceOf[KeyStore.PrivateKeyEntry]
  }

  private val cacheKeys:Cache[String] = new ExpiringLruCache[String](10000, 0, 30 minutes, 10 minutes)

  def isInitialized:Boolean = {
    pkEntry.getPrivateKey != null
  }

  def encrypt (data:String):String = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, pkEntry.getCertificate)
    val cipherData = cipher.doFinal(data.getBytes())
    Base64.getEncoder.encodeToString(cipherData)
  }

  def decrypt(data:String): Future[String] = cacheKeys(data) {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, pkEntry.getPrivateKey)
    val ret = new String(cipher.doFinal(Base64.getDecoder.decode(data)))
    ret
  }
  
  def remove (data: String):Unit = {
    cacheKeys.remove(data)
  }
}
