if(NOT TARGET hermes-engine::libhermes)
add_library(hermes-engine::libhermes SHARED IMPORTED)
set_target_properties(hermes-engine::libhermes PROPERTIES
    IMPORTED_LOCATION "/Users/shawnshaw/.gradle/caches/8.10.2/transforms/048bb42d3e04d7d764b32dfbf1a4cc6f/transformed/jetified-hermes-android-0.76.5-debug/prefab/modules/libhermes/libs/android.armeabi-v7a/libhermes.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/shawnshaw/.gradle/caches/8.10.2/transforms/048bb42d3e04d7d764b32dfbf1a4cc6f/transformed/jetified-hermes-android-0.76.5-debug/prefab/modules/libhermes/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

