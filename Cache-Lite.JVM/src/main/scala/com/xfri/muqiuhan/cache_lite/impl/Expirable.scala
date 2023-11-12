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
import java.util.concurrent.TimeUnit

/** [ExpirableCache] flushes the items whose life time is longer than
  * [flushInterval].
  */
class Expirable[K, V](
    delegate: GenericCache[K, V],
    flushInterval: Long = Expirable.DEFAULT_FLUSH_INTERVAL
) extends GenericCache[K, V]:
  private var lastFlushTime = System.nanoTime()

  private def recycle(): Unit =
    if (System.nanoTime() - lastFlushTime) >= TimeUnit.MILLISECONDS.toNanos(
        flushInterval
      )
    then
      delegate.clear()
      lastFlushTime = System.nanoTime()

  override def size(): Int =
    recycle()
    delegate.size()

  override def remove(key: K): Option[V] =
    recycle()
    delegate.remove(key)

  override def get(key: K): Option[V] =
    recycle()
    delegate.get(key)

  override def clear(): Unit = delegate.clear()
  override def set(key: K, value: V): Unit = delegate.set(key, value)

object Expirable:
  val DEFAULT_FLUSH_INTERVAL: Long = TimeUnit.MINUTES.toMillis(1)
