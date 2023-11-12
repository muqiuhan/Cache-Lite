#include <LRUCache.h>

int main()
{

    LRUCache<int,int> cache(2);
    cache.put(1, 1);
    cache.put(2, 2);
    cache.put(3, 3);
    cache.put(4, 4);
    cache.put(5, 5);
    cache.put(6, 6);
    cache.put(7, 7);
    cache.put(8, 8);
    cache.put(9, 9);
    cache.put(10, 10);
}