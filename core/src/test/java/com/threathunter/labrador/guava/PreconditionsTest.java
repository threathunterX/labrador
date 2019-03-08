package com.threathunter.labrador.guava;

import com.google.common.base.Preconditions;
import org.junit.Test;

import java.util.List;

public class PreconditionsTest {

    @Test
    public void testArgument() {
        List list = null;
        Preconditions.checkArgument(list == null, "list is null");
        System.out.println("finished");
    }

}
