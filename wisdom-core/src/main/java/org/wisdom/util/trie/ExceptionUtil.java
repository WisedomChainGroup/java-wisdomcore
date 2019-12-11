package org.wisdom.util.trie;

public class ExceptionUtil {
    public static RuntimeException keyNotFound(String key) {
        return new RuntimeException("key " + key + " not found");
    }

    public static RuntimeException noArgumentConstructorNotFound(Class clazz){
        return new RuntimeException(clazz.getName() + " not has a non-argument constructor");
    }
    public static void require(boolean b, String msg){
        if(!b) throw new RuntimeException(msg);
    }
}

