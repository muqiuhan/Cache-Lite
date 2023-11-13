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

class FIFOTest extends munit.FunSuite:

  test("Should only cache size items") {
    val cache = FIFO[Int, Int](Perpetual(), 31)
    for (i <- 0 to 99) do cache.set(i, i)
    assertEquals[Int, Int](cache.size(), 31)
  }

  test("Should only cache latest size items") {
    val cache = FIFO[Int, Int](Perpetual(), 31)
    for (i <- 0 to 99) do cache.set(i, i)

    for (i <- 0 to 99) do
      if i < (100 - 31) then
        assertEquals[Option[Int], Option[Int]](None, cache.get(i))
      else
        assertNotEquals[Option[Int], Option[Int]](None, cache.get(i))
  }

  test("Should remove entry") {
    val cache = FIFO[Int, Int](Perpetual(), 31)
    for (i <- 0 to 30) do cache.set(i, i)
    assertNotEquals[Option[Int], Option[Int]](cache.get(25), None)
    val _ = cache.remove(25)
    assertEquals[Option[Int], Option[Int]](cache.get(25), None)
  }
