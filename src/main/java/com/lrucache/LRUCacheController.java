package com.lrucache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LRUCacheController {

    @Autowired
    private LRUCacheService lruCacheService;

    private static Logger log = LoggerFactory.getLogger(LRUCacheController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/get/{key}")
    public ResponseEntity<String> getEntry(@PathVariable int key) {
        LRUCacheResponse cacheServiceResponse = lruCacheService.get(key);
        log.info("Method GET :: cacheServiceResponse {}", cacheServiceResponse);
        if (cacheServiceResponse.responseStatus == LRUCacheResponse.ResponseStatus.HIT)
            return new ResponseEntity(cacheServiceResponse, HttpStatus.OK);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/put/{key}")
    public ResponseEntity<String> putEntry(@PathVariable("key") int key,
                                           @RequestParam("value") int value) {
        LRUCacheResponse cacheServiceResponse = lruCacheService.put(key, value);
        log.info("Method PUT :: cacheServiceResponse {}", cacheServiceResponse);
        return new ResponseEntity(cacheServiceResponse, HttpStatus.OK);

    }

}
