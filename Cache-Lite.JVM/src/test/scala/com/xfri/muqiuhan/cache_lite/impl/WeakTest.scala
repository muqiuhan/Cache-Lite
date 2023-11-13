package com.xfri.muqiuhan.cache_lite.impl

class WeakTest extends munit.FunSuite:
  test("Should clear unreachable items") {
    val cache = Weak[Int, Array[Byte]](Perpetual())
    val size = 10

    {
        for (i <- 0 to size) do cache.set(i, new Array[Byte](WeakTest.ONE_MEGABYTE))
    }

    System.gc()
    assert(cache.size() < size)
  }

object WeakTest:
  val ONE_MEGABYTE = 1024 * 1024
