package net.nyhm.driveup

import com.google.api.client.util.store.DataStore
import com.google.api.client.util.store.DataStoreFactory
import com.google.protobuf.ByteString
import net.nyhm.driveup.proto.CredsData
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.Serializable

/**
 * A [DataStoreFactory] designed to serialize the user credentials to bytes, which
 * can be persisted and restored in a custom way (rather than implicitly file-based).
 */
internal class CredsStoreFactory(data: Map<String,CredsData> = emptyMap()): DataStoreFactory {

  private val stores = mutableMapOf<String,CredsStore<out Serializable>>()

  init {
    for ((key,creds) in data) {
      stores[key] = CredsStore.fromData(key, creds, this)
    }
  }

  fun export(): Map<String,CredsData> {
    val map = mutableMapOf<String,CredsData>()
    for ((key,creds) in stores) {
      map[key] = creds.export()
    }
    return map
  }

  // TODO: implementation should be thread safe, per DataStoreFactory contract
  override fun <V:Serializable> getDataStore(id: String): DataStore<V> {
    if (!stores.containsKey(id)) stores[id] = CredsStore<V>(id, this)
    return stores[id] as DataStore<V>
  }
}

// TODO: implementation should be thread safe, per DataStore contract
private class CredsStore<V:Serializable>(
    val storeId: String,
    val factory: DataStoreFactory
): DataStore<V>, Serializable
{
  companion object {
    fun <V:Serializable> fromData(storeId: String, data: CredsData, factory: DataStoreFactory): CredsStore<V> {
      val store = CredsStore<V>(storeId, factory)
      for ((key,bytes) in data.entriesMap) {
        store.data[key] = ObjectInputStream(ByteArrayInputStream(bytes.toByteArray())).use {
          it.readObject() as V
        }
      }
      return store
    }
  }

  internal fun export(): CredsData {
    val creds = CredsData.newBuilder()
    for ((key,value) in data) {
      creds.putEntries(key, ByteString.copyFrom(value.serialize()))
    }
    return creds.build()
  }

  private val data = mutableMapOf<String,V>()

  override fun getDataStoreFactory() = factory

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