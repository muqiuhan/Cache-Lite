/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Muqiu Han
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xfri.muqiuhan.cache_lite.impl

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

import com.xfri.muqiuhan.cache_lite.GenericCache
import scala.annotation.tailrec

/** [WeakCache] caches items with a [WeakReference] wrapper.
  *
  * A weak reference is a reference that isn't strong enough to force an object
  * to remain in memory. Weak references allow you to leverage the garbage
  * collector's ability to determine reachability for you, so you don't have to
  * do it yourself.
  */
class Weak[K, V](delegate: GenericCache[K, Any]) extends GenericCache[K, V]:
  private case class WeakEntry(
      key: K,
      value: V,
      referenceQueue: ReferenceQueue[Any]
  ) extends WeakReference[V](value, referenceQueue)

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

  override def get(key: K): Option[V] =
    delegate
      .get(key)
      .flatMap(value =>
        delegate.remove(key).map(_ => value.asInstanceOf[WeakEntry].value)
      )

  override def set(key: K, value: V): Unit =
    removeUnreachableItems()
    delegate.set(key, WeakEntry(key, value, referenceQueue))

  /** Since its a weak reference, the returned value is not available So [None]
    * is always returned
    */
  override def remove(key: K): Option[V] =
    delegate.remove(key)
    removeUnreachableItems()
    None
