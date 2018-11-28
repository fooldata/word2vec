package ren.cesar.algorithm;

import java.util.*;

/**
 * 通过堆算法提取距离最近的N个词
 *
 * @author 陈明超
 * @date 2018/11/23
 */
public class MaxHeap<E> implements Iterable<E> {
    /**
     * 优先队列
     */
    private PriorityQueue<E> queue;
    /**
     * 堆的最大容量
     */
    private int maxSize;

    /**
     * 构造最大堆
     *
     * @param maxSize    保留多少个元素
     * @param comparator 比较器，生成最大堆使用o1-o2，生成最小堆使用o2-o1，并修改 e.compareTo(peek) 比较规则
     */
    public MaxHeap(int maxSize, Comparator<E> comparator) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxSize = maxSize;
        this.queue = new PriorityQueue<>(maxSize, comparator);
    }

    /**
     * 获取当前堆顶元素
     *
     * @return 元素
     */
    public E getTop() {
        if (queue.size() == 0) {
            return null;
        }
        return queue.peek();
    }

    /**
     * 添加一个元素
     *
     * @param e 元素
     * @return 是否添加成功
     */
    public boolean add(E e) {
        // 未达到最大容量，直接添加
        if (queue.size() < maxSize) {
            queue.add(e);
            return true;
        } else {
            // 队列已满
            E peek = queue.peek();
            // 将新元素与当前堆顶元素比较，保留较小或较大的元素（根据比较器的定义确定）
            if (queue.comparator().compare(e, peek) > 0) {
                queue.poll();
                queue.add(e);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加许多元素
     *
     * @param collection 对象本身
     */
    public MaxHeap<E> addAll(Collection<E> collection) {
        for (E e : collection) {
            add(e);
        }

        return this;
    }

    /**
     * 转为有序列表，自毁性操作
     *
     * @return 转换后的对象
     */
    public List<E> toList() {
        ArrayList<E> list = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            list.add(0, queue.poll());
        }

        return list;
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    public int size() {
        return queue.size();
    }
}
