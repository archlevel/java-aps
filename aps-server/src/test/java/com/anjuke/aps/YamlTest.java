package com.anjuke.aps;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlTest {

    @Test
    public void test() {
        Yaml yaml=new Yaml();
        String context="context:\n" +
                "  - &name \"abc\"\n" +
                "  - &bean !!com.anjuke.aps.TestBean [ *name ]\n" +
                "test:\n"+
                "  - &re !!com.anjuke.aps.RecursiveTestBean [ *bean ]\n";
        System.out.println(context);
        Object o=yaml.load(context);
        System.out.println(o);
    }
}
