add_rules("mode.debug", "mode.release")

target("cache_lite")
    set_kind("headeronly")
    add_files("src/*.cpp")