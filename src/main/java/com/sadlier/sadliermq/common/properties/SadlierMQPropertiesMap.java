package com.sadlier.sadliermq.common.properties;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @author sang.le-hoang
 * @created Oct 15, 2021
 */
public class SadlierMQPropertiesMap extends HashMap<String, SadlierMQProperties> {
    @PostConstruct
    public void postConstructBean() {
        this.remove("shared");
    }

    public Stream<Entry<String, SadlierMQProperties>> stream() {
        return entrySet().stream();
    }
}
