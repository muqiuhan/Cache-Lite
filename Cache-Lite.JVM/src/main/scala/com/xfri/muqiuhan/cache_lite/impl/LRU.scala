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

import com.xfri.muqiuhan.cache_lite.GenericCache
import java.util.LinkedHashMap
import java.util.Map.Entry

/** [LRU] cache flushes items that are **Least Recently Used** and keeps
  * [minimalSize] items at most.
  */
class LRU[K, V](
    delegate: GenericCache[K, V],
    minimalSize: Int = LRU.DEFAULT_SIZE
) extends GenericCache[K, V]:
  private var eldestKeyToRemove: Option[K] = None

  private val cache =
    new LinkedHashMap[K, Boolean](minimalSize, 0.75f, true) {
      override protected def removeEldestEntry(
          eldest: Entry[K, Boolean]
      ): Boolean =
        val tooManyCachedItems = this.size > minimalSize

        if tooManyCachedItems then eldestKeyToRemove = Some(eldest.getKey())
        tooManyCachedItems
    }

  override def size(): Int = delegate.size()

  override def get(key: K): Option[V] =
    cache.get(key).asInstanceOf[Boolean | Null] match
      case v: Boolean => delegate.get(key)
      case _          => None

  override def set(key: K, value: V): Unit =
    delegate.set(key, value)
    cycleCache(key)

  override def clear(): Unit =
    cache.clear()
    delegate.clear()

  override def remove(key: K): Option[V] = delegate.remove(key)

  private def cycleCache(key: K): Unit =
    cache.put(key, LRU.PRESENT)
    eldestKeyToRemove.foreach(delegate.remove(_))
    eldestKeyToRemove = None

object LRU:
  val DEFAULT_SIZE: Int = 1024
  val PRESENT: Boolean = true
