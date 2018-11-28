package ren.cesar.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ren.cesar.algorithm.MaxHeap;
import ren.cesar.kdtree.KdNode;
import ren.cesar.kdtree.KdTree;
import ren.cesar.kdtree.WordVector;

import java.io.IOException;
import java.util.*;

/**
 * 简化版本的词向量
 *
 * @author 陈明超
 * @date 2018/11/23
 */
public class Word2VecModel {

    private Map<String, WordVector> storage;
    private KdTree kdTree;

    private Logger logger = LoggerFactory.getLogger(Word2VecModel.class);

    /**
     * 加载模型<br>
     *
     * @param modelFileName 模型路径
     * @throws IOException 加载错误
     */
    public Word2VecModel(String modelFileName) throws IOException {
        loadVectorMap(modelFileName);
    }

    /**
     * 获取一个键的向量（键不会被预处理）
     *
     * @param key 键
     * @return 向量
     */
    public WordVector vector(String key) {
        return storage.get(key);
    }

    /**
     * 余弦相似度
     *
     * @param what 一个词
     * @param with 另一个词
     * @return 余弦相似度
     */
    public float similarity(String what, String with) {
        WordVector wordVectorWhat = storage.get(what);
        if (wordVectorWhat == null) {
            return -1f;
        }
        WordVector wordVectorWith = storage.get(with);
        if (wordVectorWith == null) {
            return -1f;
        }
        return wordVectorWhat.cosineForUnitVector(wordVectorWith);
    }

    /**
     * 查询与key最相似的元素
     *
     * @param key  键
     * @param size topN个
     * @return 键值对列表, 键是相似词语, 值是相似度, 按相似度降序排列
     */
    public Map<String, Float> nearest(String key, int size) {
        WordVector wordVector = storage.get(key);
        if (wordVector == null) {
            return Collections.emptyMap();
        }
        return nearest(key, wordVector, size);
    }

    /**
     * 获取与向量最相似的词语
     *
     * @param wordVector 向量
     * @param size       topN个
     * @return 键值对列表, 键是相似词语, 值是相似度, 按相似度降序排列
     */
    public Map<String, Float> nearest(WordVector wordVector, int size) {
        Map<String, Float> result = new LinkedHashMap<>(size);
        MaxHeap<Map.Entry<KdNode, Float>> maxHeap = kdTree.getNearVectors(size, wordVector);
        for (Map.Entry<KdNode, Float> entry : maxHeap.toList()) {
            result.put(entry.getKey().getWord(), wordVector.cosineForUnitVector(entry.getKey().getWordVector()));
        }
        return result;
    }

    /**
     * 获取与向量最相似的词语（默认10个）
     *
     * @param wordVector 向量
     * @return 键值对列表, 键是相似词语, 值是相似度, 按相似度降序排列
     */
    public Map<String, Float> nearest(WordVector wordVector) {
        return nearest(wordVector, 10);
    }

    /**
     * 查询与词语最相似的词语
     *
     * @param key 词语
     * @return 键值对列表, 键是相似词语, 值是相似度, 按相似度降序排列
     */
    public Map<String, Float> nearest(String key) {
        return nearest(key, 10);
    }

    /**
     * 模型中的词向量总数（词表大小）
     *
     * @return 大小
     */
    public int size() {
        return storage.size();
    }

    /**
     * 模型中的词向量维度
     *
     * @return 维度
     */
    public int dimension() {
        if (storage == null || storage.isEmpty()) {
            return 0;
        }
        return storage.values().iterator().next().size();
    }

    /**
     * 加载文本格式的词向量
     *
     * @param modelFilePath 路径
     * @return 词向量的map
     * @throws IOException 加载出错
     */
    private void loadVectorMap(String modelFilePath) throws IOException {
        VectorsReader reader = new VectorsReader(modelFilePath);
        reader.readVectorFile();
        this.storage = new TreeMap<>();
        for (int i = 0; i < reader.getVocab().length; i++) {
            storage.put(reader.getVocab()[i], new WordVector(reader.getMatrix()[i]));
        }
        logger.info("词向量加载完毕，开始构建KD树");
        kdTree = new KdTree(reader.getArraySize());
        for (Map.Entry<String, WordVector> entry : this.storage.entrySet()) {
            kdTree.insert(entry.getValue(), entry.getKey());
        }
        logger.info("构建KD树完毕");
    }

    /**
     * 查询与key最相似的元素
     *
     * @param key        键 结果将排除该键
     * @param wordVector 向量
     * @param size       topN个
     * @return 键值对列表, 键是相似词语, 值是相似度, 按相似度降序排列
     */
    private Map<String, Float> nearest(String key, WordVector wordVector, int size) {
        Map<String, Float> result = new LinkedHashMap<>(size);

        MaxHeap<Map.Entry<KdNode, Float>> maxHeap = kdTree.getNearVectors(size + 1, wordVector);
        for (Map.Entry<KdNode, Float> entry : maxHeap.toList()) {
            if (entry.getKey().getWord().equals(key)) {
                continue;
            }
            result.put(entry.getKey().getWord(), similarity(key, entry.getKey().getWord()));
        }
        return result;
    }


    /**
     * 删除元素
     *
     * @param key 关键词
     * @return 删除的元素
     */
    public WordVector remove(String key) {
        return storage.remove(key);
    }

    public boolean hasWord(String key) {
        return storage.get(key) != null;
    }

    /**
     * 获取一列词汇的中心向量
     */
    public WordVector centerWordList(List<String> wordList) {
        if (wordList == null || wordList.size() == 0) {
            return null;
        }
        WordVector wordVector = null;
        int totalSize = 0;
        for (String word : wordList) {
            WordVector wordVectorNew = this.vector(word.toUpperCase());
            if (wordVectorNew != null) {
                if (wordVector == null) {
                    wordVector = wordVectorNew;
                } else {
                    wordVector.addToSelf(wordVectorNew);
                }
                totalSize++;
            }
        }
        if (wordVector == null) {
            return null;
        }
        return wordVector.divideToSelf(totalSize);
    }
}
