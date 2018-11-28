package ren.cesar.kdtree;

import ren.cesar.algorithm.MaxHeap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 构建kd tree 数据格式
 *
 * @author 陈明超
 * @date 2018-11-24
 */
public class KdNode {

    /**
     * 词
     */
    private String word;
    /**
     * 当前词向量
     */
    private WordVector wordVector;

    /**
     * 选择的维度
     */
    private int dim;
    /**
     * 记录左子树和右子树
     */
    private KdNode leftNode, rightNode, parentNode;


    private KdNode(String word, WordVector wordVector, KdNode parentNode, int dim) {
        this.word = word;
        this.wordVector = wordVector;
        this.leftNode = null;
        this.rightNode = null;
        this.parentNode = parentNode;
        this.dim = dim;
    }

    static KdNode create(String word, WordVector wordVector, KdNode parentNode, int dim) {
        return new KdNode(word, wordVector, parentNode, dim);
    }

    static int insert(WordVector wordVector, String word, KdNode parentNode, int dim, int count) {
        KdNode nextNode;
        int nextDim = (dim + 1) % count;
        if (wordVector.equals(parentNode.getWordVector())) {
            parentNode.setWord(word);
            return 0;
        }
        if (wordVector.getElementArray()[dim] > parentNode.getWordVector().getElementArray()[dim]) {
            nextNode = parentNode.getRightNode();
            if (nextNode == null) {
                parentNode.setRightNode(KdNode.create(word, wordVector, parentNode, dim));
                return 1;
            }
        } else {
            nextNode = parentNode.leftNode;
            if (nextNode == null) {
                parentNode.setLeftNode(KdNode.create(word, wordVector, parentNode, dim));
                return 1;
            }
        }
        return insert(wordVector, word, nextNode, nextDim, count);
    }


    /**
     * @param minHeap          结果保存对象,最小堆
     * @param targetWordVector 待查找向量
     * @param topN             前K个词
     */
    static void getNearNodes(MaxHeap<Map.Entry<KdNode, Float>> minHeap, KdNode rootNode, WordVector targetWordVector, int topN) {
        KdNode leafNode = getLeafNode(rootNode, targetWordVector);
        while (leafNode.getParentNode() != null && !leafNode.getWordVector().equals(rootNode.getWordVector())) {
            // 得到半径
            float r = Float.MAX_VALUE;
            if (minHeap.size() >= topN) {
                r = minHeap.getTop().getValue();
            }
            // 计算当前节点父节点与target的距离
            float distance = leafNode.getParentNode().getWordVector().squaredDistance(targetWordVector);
            if (distance <= r) {
                minHeap.add(new AbstractMap.SimpleEntry<>(leafNode.getParentNode(), distance));
            }
            KdNode brotherNode = getBrother(leafNode);
            // 检查兄弟节点的超平面空间是否与当前目标点为球心，目标点与“当前最近点”间的距离为半径的超球体相交
            if (brotherNode != null && distance > Math.abs(targetWordVector.getElementArray()[leafNode.getParentNode().getDim()] - leafNode.getParentNode().getWordVector().getElementArray()[leafNode.getParentNode().getDim()])) {
                getNearNodes(minHeap, brotherNode, targetWordVector, topN);
            }
            leafNode = leafNode.getParentNode();
        }
    }


    /**
     * 获取兄弟节点
     *
     * @param node
     * @return
     */
    public static KdNode getBrother(KdNode node) {
        if (node == node.parentNode.leftNode) {
            return node.parentNode.rightNode;
        } else {
            return node.parentNode.leftNode;
        }
    }

    /**
     * 拿到所有子节点
     */
    static List<KdNode> getAllChildNode(KdNode rootNode) {
        List<KdNode> kdNodeList = new ArrayList<>();
        if (rootNode.getRightNode() != null) {
            kdNodeList.addAll(getAllChildNode(rootNode.getRightNode()));
        }
        if (rootNode.getLeftNode() != null) {
            kdNodeList.addAll(getAllChildNode(rootNode.getLeftNode()));
        }
        kdNodeList.add(rootNode);
        return kdNodeList;
    }

    /**
     * 拿到叶子节点
     *
     * @param rootNode         根节点
     * @param targetWordVector 目标向量
     */
    static KdNode getLeafNode(KdNode rootNode, WordVector targetWordVector) {
        KdNode kdNode = rootNode;
        while (true) {
            if (targetWordVector.getElementArray()[kdNode.getDim()] > kdNode.getWordVector().getElementArray()[kdNode.getDim()]) {
                if (kdNode.getRightNode() == null) {
                    return kdNode;
                }
                kdNode = kdNode.getRightNode();
            } else {
                if (kdNode.getLeftNode() == null) {
                    return kdNode;
                }
                kdNode = kdNode.getLeftNode();
            }
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordVector getWordVector() {
        return wordVector;
    }

    public void setWordVector(WordVector wordVector) {
        this.wordVector = wordVector;
    }

    public KdNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(KdNode leftNode) {
        this.leftNode = leftNode;
    }

    public KdNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(KdNode rightNode) {
        this.rightNode = rightNode;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public KdNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(KdNode parentNode) {
        this.parentNode = parentNode;
    }
}
