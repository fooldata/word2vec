package org.fooldata.kdtree;

import java.util.Arrays;

/**
 * @author 陈明超
 * @date 2018/11/23
 */
public class WordVector {

    private float[] elementArray;

    public WordVector(float[] elementArray) {
        this.elementArray = elementArray;
    }

    public WordVector(int size) {
        elementArray = new float[size];
        Arrays.fill(elementArray, 0);
    }

    public int size() {
        return elementArray.length;
    }

    public float dot(WordVector other) {
        float ret = 0.0f;
        for (int i = 0; i < size(); ++i) {
            ret += elementArray[i] * other.elementArray[i];
        }
        return ret;
    }

    public float norm() {
        float ret = 0.0f;
        for (int i = 0; i < size(); ++i) {
            ret += elementArray[i] * elementArray[i];
        }
        return (float) Math.sqrt(ret);
    }

    /**
     * 夹角的余弦<br>
     * 认为this和other都是单位向量，所以方法内部没有除以两者的模。
     *
     * @param other 其他单位向量
     * @return 余弦
     */
    public float cosineForUnitVector(WordVector other) {
        return dot(other);
    }

    /**
     * 夹角的余弦<br>
     *
     * @param other 其他向量
     * @return 余弦
     */
    public float cosine(WordVector other) {
        return dot(other) / this.norm() / other.norm();
    }

    public WordVector minus(WordVector other) {
        float[] result = new float[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = elementArray[i] - other.elementArray[i];
        }
        return new WordVector(result);
    }

    public WordVector add(WordVector other) {
        float[] result = new float[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = elementArray[i] + other.elementArray[i];
        }
        return new WordVector(result);
    }

    public WordVector addToSelf(WordVector other) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] + other.elementArray[i];
        }
        return this;
    }

    public WordVector divideToSelf(int n) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] / n;
        }
        return this;
    }

    public WordVector divideToSelf(float f) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] / f;
        }
        return this;
    }

    public float squaredDistance(WordVector other) {
        if (this.getElementArray().length != other.getElementArray().length) {
            return Float.MAX_VALUE;
        }
        float result = Float.MAX_VALUE;
        for (int i = 0; i < other.getElementArray().length; i++) {
            result += Math.pow(this.getElementArray()[i] - other.getElementArray()[i], 2);
        }
        return (float) Math.sqrt(result);
    }

    /**
     * 自身归一化
     *
     * @return
     */
    public WordVector normalize() {
        divideToSelf(norm());
        return this;
    }

    public float[] getElementArray() {
        return elementArray;
    }

    public void setElementArray(float[] elementArray) {
        this.elementArray = elementArray;
    }

    public boolean equals(WordVector wordVector) {
        if (this == wordVector) {
            return true;
        }
        if (wordVector == null || getClass() != wordVector.getClass()) {
            return false;
        }
        return Arrays.equals(elementArray, wordVector.elementArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elementArray);
    }
}
