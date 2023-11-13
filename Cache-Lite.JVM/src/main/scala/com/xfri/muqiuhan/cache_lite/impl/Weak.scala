package com.xfri.muqiuhan.cache_lite.impl

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

import com.xfri.muqiuhan.cache_lite.Cache
import scala.annotation.tailrec

/** [WeakCache] caches items with a [WeakReference] wrapper.
  *
  * A weak reference is a reference that isn't strong enough to force an object
  * to remain in memory. Weak references allow you to leverage the garbage
  * collector's ability to determine reachability for you, so you don't have to
  * do it yourself.
  */
class Weak(delegate: Cache) extends Cache:
  private case class WeakEntry(
      key: Any,
      value: Any,
      referenceQueue: ReferenceQueue[Any]
  ) extends WeakReference[Any](value, referenceQueue)

  private val referenceQueue = ReferenceQueue[Any]()

  /** This implementation may be performance issues :)
    *
    * private def removeUnreachableItems(): Unit =
    * @tailrec
    *   def remove(entry: WeakEntry | Null): Unit = entry match case null => ()
    *   case entry: WeakEntry => delegate.remove(entry.key)
    *   remove(referenceQueue.poll().asInstanceOf[WeakEntry | Null])
    *
    * remove(referenceQueue.poll().asInstanceOf[WeakEntry | Null])
    */
  private def removeUnreachableItems(): Unit =
    while (
      referenceQueue.poll().asInstanceOf[WeakEntry | Null] match
        case null => false
        case entry: WeakEntry =>
          delegate.remove(entry.key)
          true
    ) do ()

  override def size(): Int = delegate.size()
  override def clear(): Unit = delegate.clear()

  override def get(key: Any): Option[Any] =
    delegate.get(key).flatMap(value => delegate.remove(key).map(_ => value))

  override def set(key: Any, value: Any): Unit =
    removeUnreachableItems()
    delegate.set(key, WeakEntry(key, value, referenceQueue))

  /** Since its a weak reference, the returned value is not available So [None]
    * is always returned
    */
  override def remove(key: Any): Option[Any] =
    delegate.remove(key)
    removeUnreachableItems()
    None
