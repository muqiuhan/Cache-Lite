add_rules("mode.debug", "mode.release")

add_requires("Catch 3.4.0")

target("cache_lite")
    set_kind("headeronly")
    add_files("src/*.cpp")

target("test")
  set_group("test")
  add_includedirs("src")
  add_files("test/*.cpp")
  add_packages("Catch 3.4.0")