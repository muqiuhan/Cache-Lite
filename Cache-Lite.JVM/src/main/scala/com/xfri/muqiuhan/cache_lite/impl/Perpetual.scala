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

import java.util.HashMap
import com.xfri.muqiuhan.cache_lite.GenericCache;

/** [PerpetualCache] caches the items perpetually unless they're manually
  * [remove]ed.
  */
class Perpetual[K, V] extends GenericCache[K, V] {
  private val cache = HashMap[K, V]()

  override val size: Int = cache.size()

  override def set(key: K, value: V): Unit = {
    cache.put(key, value)
  }

  override def remove(key: K): V = {
    cache.remove(key)
  }

  override def get(key: K): Option[V] = {
    cache.get(key) match {
      case value: V => Some(value)
      case null     => None
    }
  }

  override def clear(): Unit = {
    cache.clear()
  }
}
