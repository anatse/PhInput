package ru.sbt.security

import java.io.FileInputStream
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher

import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import java.util.{Map, LinkedHashMap}
import scala.concurrent._

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

  def makeCache[K,V](capacity: Int): Map[K, V] = {
    new LinkedHashMap[K, V](capacity, 0.7F, true) {
      private val cacheCapacity = capacity
      override def removeEldestEntry(entry: Map.Entry[K, V]): Boolean = {
        this.size() > this.cacheCapacity
      }
    }
  }

  private lazy val cacheKeys:Map[String, String] = makeCache(10000)
  private def cache (key:String)(cachedFunc:String => Future[String]):Future[String] = {
    if (cacheKeys.containsKey(key)) {
      Future(cacheKeys.get(key))
    }
    else {
      val value = cachedFunc (key)
      cacheKeys.put (key, Await.result (value, 10.seconds))
      value
    }
  }

  def isInitialized:Boolean = {
    pkEntry.getPrivateKey() != null
  }

  def encrypt (data:String):String = {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, pkEntry.getCertificate)
    val cipherData = cipher.doFinal(data.getBytes())
    Base64.getEncoder.encodeToString(cipherData)
  }

  def decrypt(data:String): Future[String] = cache(data) {
    data => {
      val cipher = Cipher.getInstance("RSA")
      cipher.init(Cipher.DECRYPT_MODE, pkEntry.getPrivateKey)
      val ret = new String(cipher.doFinal(Base64.getDecoder.decode(data)))
      Future(ret)
    }
  }
  
  def remove (data: String):Unit = {
    cacheKeys.remove(data)
  }
}
