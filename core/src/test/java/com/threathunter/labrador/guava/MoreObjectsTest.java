package com.threathunter.labrador.guava;

import com.threathunter.model.Event;
import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class MoreObjectsTest {

    @Test
    public void testMoreObject() throws IOException {
        String eventString = MoreObjects.toStringHelper(Event.class).omitNullValues().toString();
        System.out.println(eventString);
        RateLimiter.create(0.5);

        WatchService watchService = FileSystems.getDefault().newWatchService();
    }
}
