package org.fooldata.modal;

import org.fooldata.model.Word2VecModel;
import org.junit.Test;

import java.io.IOException;

/**
 * @author 陈明超
 * @date 2018/12/26
 */
public class VectorModalTest {

    @Test
    public void test1() {
        try {
            String path1 = "C:\\Users\\Administrator\\Desktop\\w2v\\sgns.sikuquanshu.word";
            String path2 = "D:\\data\\model\\word2vec.vector.v3.1_600_10_5_1";
            Word2VecModel word2VecModel = new Word2VecModel(path2);
            System.out.println(word2VecModel.size());
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                System.out.println(word2VecModel.nearest("机器学习", 1));
                System.out.println(word2VecModel.nearest2("机器学习"));
            }
            System.out.println("耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
