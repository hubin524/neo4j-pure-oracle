package com.tydic.framework.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BatchUtil
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */

public class BatchUtil {

    public BatchUtil() {
    }

    public static <E> List<List<E>> batch(List<E> data, int batchSize) {
        List<List<E>> res = new ArrayList();
        List<E> batchData = new ArrayList();

        for(int i = 0; i < data.size(); ++i) {
            E temp = data.get(i);
            batchData.add(temp);
            if (batchData.size() == batchSize || i == data.size() - 1) {
                res.add(batchData);
                batchData = new ArrayList();
            }
        }

        return res;
    }
}
