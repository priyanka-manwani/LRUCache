package com.lrucache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LRUCacheService {

    private static Logger log = LoggerFactory.getLogger(LRUCacheService.class);

    class Entry {
        int value;
        int key;
        Entry left;
        Entry right;

    }

    @Value("${lru.cache.size}")
    private int LRU_CACHE_SIZE;

    private HashMap<Integer, Entry> entryMap = new HashMap<>();
    private Entry start, end;

    public LRUCacheResponse get(int key) {
        if (entryMap.containsKey(key)) // Key found, Hit
        {
            Entry entry = entryMap.get(key);
            removeNode(entry);
            addAtTop(entry);
            log.debug("Method get :: HIT for key {}", key, " value is {}", entry.value);
            return new LRUCacheResponse(LRUCacheResponse.ResponseStatus.HIT, key, entry.value);
        }
        log.debug("Method get :: MISS for key {}", key);
        return new LRUCacheResponse(LRUCacheResponse.ResponseStatus.MISS);
    }

    public LRUCacheResponse put(int key, int value) {
        if (entryMap.containsKey(key)) // Key Already Exist, just update the value and move it to top
        {
            log.debug("Method put :: Map contains key", key);
            Entry entry = entryMap.get(key);
            entry.value = value;
            removeNode(entry);
            addAtTop(entry);
        } else {
            Entry newnode = new Entry();
            newnode.left = null;
            newnode.right = null;
            newnode.value = value;
            newnode.key = key;
            if (entryMap.size() >= LRU_CACHE_SIZE) // We have reached maxium size so need to make room for new element.
            {
                int evictedKey = end.key;
                int evictedValue = end.value;
                log.debug("Method put:: Evicting key {}", evictedKey, " with value {}", evictedValue);
                entryMap.remove(end.key);
                removeNode(end);
                addAtTop(newnode);
                entryMap.put(key, newnode);
                return new LRUCacheResponse(LRUCacheResponse.ResponseStatus.EVICTED, evictedKey, evictedValue);
            } else {
                addAtTop(newnode);
                entryMap.put(key, newnode);
            }
            log.debug("Method put :: Added new Entry with key {}", key, " value {}", value);
        }
        return new LRUCacheResponse(LRUCacheResponse.ResponseStatus.PUT_SUCCESSFUL);
    }

    private void addAtTop(Entry node) {
        node.right = start;
        node.left = null;
        if (start != null)
            start.left = node;
        start = node;
        if (end == null)
            end = start;
    }

    private void removeNode(Entry node) {

        if (node.left != null) {
            node.left.right = node.right;
        } else {
            start = node.right;
        }

        if (node.right != null) {
            node.right.left = node.left;
        } else {
            end = node.left;
        }
    }


}


