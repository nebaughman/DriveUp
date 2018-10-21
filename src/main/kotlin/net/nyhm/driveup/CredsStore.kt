package net.nyhm.driveup

import com.google.api.client.util.store.DataStore
import com.google.api.client.util.store.DataStoreFactory
import java.io.*

/**
 * A [DataStoreFactory] designed to serialize the user credentials to bytes, which
 * can be persisted and restored in a custom way (rather than implicitly file-based).
 */
object CredsStoreFactory: DataStoreFactory {

  /**
   * Version prepended to exported data to allow for future serialization schemes
   */
  private const val version = 1

  private val stores = mutableMapOf<String,CredsStore<out Serializable>>()

  // TODO: implementation should be thread safe, per DataStoreFactory contract
  override fun <V:Serializable> getDataStore(id: String): DataStore<V> {
    if (!stores.containsKey(id)) stores[id] = CredsStore<V>(id)
    return stores[id] as DataStore<V>
  }

  // TODO: This factory is a store of stores. Add CredsStore.export():ByteArray,
  // which uses an ObjectOutputStream for _each_ of its entries, capturing each to
  // its own ByteArray; saving each (key,ByteArray) pair to a DataOutputStream.
  // Then, CredsStoreFactory saves each of its (key,ByteArray) similarly to a
  // DataOutputStream. This is more work, but avoids the pitfalls of ObjectOutputStream
  // (ie, cannot refactor CredsStore without corrupting restore; unless you save a
  // serialVersionUID, which is a pain to work with; better to version raw bytes).
  //
  fun export(): ByteArray {
    val baos = ByteArrayOutputStream()
    ObjectOutputStream(baos).use {
      it.writeInt(version)
      it.writeObject(stores)
    }
    return baos.toByteArray()
  }

  /**
   * Restore this (singleton) instance with the given exported state.
   */
  fun restore(bytes: ByteArray) {
    stores.clear()
    if (bytes.isEmpty()) return
    ObjectInputStream(ByteArrayInputStream(bytes)).use {
      val ver = it.readInt()
      if (ver != version) throw IllegalArgumentException("Invalid version $ver, expecting $version")
      val map = it.readObject() as Map<*,*>
      for ((key,store) in map) {
        stores[key as String] = store as CredsStore<out Serializable>
      }
    }
  }
}

// TODO: implementation should be thread safe, per DataStore contract
private class CredsStore<V:Serializable>(val storeId: String): DataStore<V>, Serializable
{
  //constructor(): this("") // empty constructor needed for (de)serialization

  private val data = mutableMapOf<String,V>()

  override fun getDataStoreFactory() = CredsStoreFactory

  override fun getId() = storeId

  override fun clear() = apply { data.clear() }

  override fun set(key: String, value: V) = apply { data[key] = value }

  override fun values() = data.values

  override fun keySet() = data.keys

  override fun get(key: String) = data[key]

  override fun delete(key: String) = apply { data.remove(key) }

  override fun isEmpty() = data.isEmpty()

  override fun containsValue(value: V) = data.containsValue(value)

  override fun size() = data.size

  override fun containsKey(key: String) = data.containsKey(key)
}