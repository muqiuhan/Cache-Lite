package com.xfri.muqiuhan.cache_lite.impl

import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import com.xfri.muqiuhan.cache_lite.GenericCache

/** [SoftCache] caches items with a [SoftReference] wrapper.
  *
  * A soft reference is exactly like a weak reference, except that it is less
  * eager to throw away the object to which it refers. An object which is only
  * weakly reachable (the strongest references to it are WeakReferences) will be
  * discarded at the next garbage collection cycle, but an object which is
  * softly reachable will generally stick around for a while.
  */
class Soft[K, V](delegate: GenericCache[K, Any]) extends GenericCache[K, V]:
  private case class SoftEntry(
      key: K,
      value: V,
      referenceQueue: ReferenceQueue[Any]
  ) extends SoftReference[V](value, referenceQueue)

  private val referenceQueue = ReferenceQueue[Any]()

  private def removeUnreachableItems(): Unit =
    while (
      referenceQueue.poll().asInstanceOf[SoftEntry | Null] match
        case null => false
        case entry: SoftEntry =>
          delegate.remove(entry.key)
          true
    ) do ()

  override def size(): Int = delegate.size()
  override def clear(): Unit = delegate.clear()

  override def get(key: K): Option[V] =
    delegate
      .get(key)
      .flatMap(value =>
        delegate.remove(key).map(_ => value.asInstanceOf[SoftEntry].value)
      )

  override def set(key: K, value: V): Unit =
    removeUnreachableItems()
    delegate.set(key, SoftEntry(key, value, referenceQueue))

  override def remove(key: K): Option[V] =
    delegate.remove(key)
    removeUnreachableItems()
    None
