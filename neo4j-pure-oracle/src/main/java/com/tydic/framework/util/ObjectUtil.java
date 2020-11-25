package com.tydic.framework.util;

/**
 * @ClassName: ObjectUtil
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */


import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectUtil {
    public ObjectUtil() {
    }

    public static boolean isEmpty(Object... objects) {
        if (objects == null) {
            return true;
        } else {
            boolean res = true;
            Object[] var2 = objects;
            int var3 = objects.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object o = var2[var4];
                if (isNotEmpty(o)) {
                    res = false;
                    break;
                }
            }

            return res;
        }
    }

    public static boolean isEmpty(Object temp) {
        boolean res = false;
        if (temp == null) {
            res = true;
        }

        if (temp instanceof String) {
            if ("".equals(temp)) {
                return true;
            }
        } else if (temp instanceof List) {
            List list = (List)temp;
            if (list.size() == 0) {
                return true;
            }
        } else if (temp instanceof Map) {
            Map map = (Map)temp;
            if (map.size() == 0) {
                return true;
            }
        } else if (temp instanceof Set) {
            Set set = (Set)temp;
            if (set.size() == 0) {
                return true;
            }
        }

        return res;
    }

    public static boolean isNotEmpty(Object temp) {
        return !isEmpty(temp);
    }

    public static boolean isNotEmpty(Object... temp) {
        if (temp == null) {
            return false;
        } else {
            boolean res = false;
            Object[] var2 = temp;
            int var3 = temp.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object o = var2[var4];
                if (isEmpty(o)) {
                    res = false;
                    break;
                }

                res = true;
            }

            return res;
        }
    }

    public static <T> T setValue(T... values) {
        T res = null;
        if (values != null && values.length != 0) {
            Object[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                T value = (T) var2[var4];
                res = value;
                if (isNotEmpty(value)) {
                    break;
                }
            }
        }

        return res;
    }

    public static <E> boolean equal(E a, E b) {
        if (a == null && b == null) {
            return true;
        } else if (a == null && b != null) {
            return false;
        } else if (a != null && b == null) {
            return false;
        } else if (a != null) {
            return a.equals(b);
        } else {
            return b != null ? b.equals(a) : false;
        }
    }

    public static <E> int compare(E a, E b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null && b != null) {
            return -1;
        } else if (a != null && b == null) {
            return 1;
        } else {
            Comparable c1 = (Comparable)a;
            Comparable c2 = (Comparable)b;
            if (c1 != null) {
                return c1.compareTo(c2);
            } else {
                return c2 != null ? c2.compareTo(c1) : 0;
            }
        }
    }

}