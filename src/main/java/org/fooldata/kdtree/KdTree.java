package org.fooldata.kdtree;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fooldata.algorithm.MaxHeap;

import java.util.Map;

/**
 * @author 陈明超
 * @date 2018-11-24
 */
public class KdTree {

    private Logger logger = LoggerFactory.getLogger(KdTree.class);

    /**
     * 根节点
     */
    private KdNode rootNode;

    /**
     * 总维度数量
     */
    private final int kDimensions;


    /**
     * nodes总数
     */
    private int nodesCount;

    /**
     * 插入一条向量
     * TODO 有时间可以改为一个平衡KD树
     *
     * @param wordVector 向量
     * @param word   词
     */
    public void insert(WordVector wordVector, String word) {
        if (wordVector.getElementArray().length != kDimensions) {
            return;
        }
        if (rootNode == null) {
            rootNode = KdNode.create(word, wordVector, null, 0);
            nodesCount++;
        } else {
            nodesCount += KdNode.insert(wordVector, word, rootNode, 0, nodesCount);
        }
    }

    /**
     * K近邻查找
     *
     * @param topN   前topN个
     * @param wordVector 向量
     * @return 排好序的KdNode
     */
    public MaxHeap<Map.Entry<KdNode, Float>> getNearVectors(int topN, WordVector wordVector) {
        // 1. 构建一个最小堆
        MaxHeap<Map.Entry<KdNode, Float>> result = new MaxHeap<>(topN, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        if (this.rootNode == null) {
            logger.error("KD树中无节点，无法查询！");
        }
        KdNode.getNearNodes(result, this.rootNode, wordVector);
        return result;
    }


    public KdTree(int kDimensions) {
        this.kDimensions = kDimensions;
        rootNode = null;
    }

    public KdNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(KdNode rootNode) {
        this.rootNode = rootNode;
    }

    public int getkDimensions() {
        return kDimensions;
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
    }
}
