package com.tydic.framework.util;

/**
 * @ClassName: GroupUtil
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupUtil {
    public GroupUtil() {
    }

    public static <K, E> Map<K, E> mapByKey(Collection<E> datas, GroupUtil.GeneratorBeanKey<K, E> generatorBeanKey) {
        Map<K, E> res = new LinkedHashMap();
        mapByKey(res, datas, generatorBeanKey);
        return res;
    }

    public static <K, E> void mapByKey(Map<K, E> container, Collection<E> datas, GroupUtil.GeneratorBeanKey<K, E> generatorBeanKey) {
        if (datas != null) {
            Iterator var3 = datas.iterator();

            while(var3.hasNext()) {
                E e = (E) var3.next();
                container.put(generatorBeanKey.key(e), e);
            }
        }

    }

    public static <K, V, E> List<E> group(Map<K, List<E>> container, K k) {
        List<E> res = null;
        if (container.containsKey(k)) {
            res = (List)container.get(k);
        } else {
            res = new ArrayList();
            container.put(k, res);
        }

        return (List)res;
    }

    public static <K, V, E> Set<E> groupSet(Map<K, Set<E>> container, K k) {
        Set<E> res = null;
        if (container.containsKey(k)) {
            res = (Set)container.get(k);
        } else {
            res = new HashSet();
            container.put(k, res);
        }

        return (Set)res;
    }

    public interface GeneratorBeanKey<K, Bean> {
        K key(Bean bean);
    }
}

