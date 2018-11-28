package ren.cesar.util;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author 陈明超
 * @date 2018/11/23
 */
public class IoUtils {

    private Logger logger = LoggerFactory.getLogger(VectorsReader.class);

    private final static Charset ENCODING = Charset.forName("UTF-8");
    private int wordSize, arraySize;
    private String[] vocab;
    private float[][] matrix;
    private final String file;

    IoUtils(String file) {
        this.file = file;
    }

    class CounterLine implements LineProcessor<Integer> {
        private int rowNum = 0;
        private int index = 0;

        @Override
        public boolean processLine(String line) throws IOException {
            if (rowNum == 0) {
                wordSize = Integer.parseInt(line.split("\\s+")[0].trim());
                arraySize = Integer.parseInt(line.split("\\s+")[1].trim());
                vocab = new String[wordSize];
                matrix = new float[wordSize][];
            } else {
                if (rowNum > 10000) {
                    wordSize--;
                    return true;
                }
                String[] params = line.split("\\s+");
                if (params.length != arraySize + 1) {
                    logger.info("词向量有一行格式不规范（可能是单词含有空格）：" + line);
                    --wordSize;
                    return true;
                }
                vocab[index] = params[0];
                matrix[index] = new float[arraySize];
                double len = 0;
                for (int j = 0; j < arraySize; j++) {
                    matrix[index][j] = Float.parseFloat(params[j + 1]);
                    len += matrix[index][j] * matrix[index][j];
                }
                len = Math.sqrt(len);
                for (int j = 0; j < arraySize; j++) {
                    matrix[index][j] /= len;
                }
                index++;
            }
            rowNum++;
            return true;
        }

        @Override
        public Integer getResult() {
            logger.info("w2v文件加载完毕，共{}行", rowNum);
            return rowNum;
        }
    }

    public void readVectorFile() throws IOException {
        try {
            Files.asCharSource(new File(file), ENCODING).readLines(new CounterLine());
            if (wordSize != vocab.length) {
                vocab = ArrayUtils.subarray(vocab, 0, wordSize);
                matrix = ArrayUtils.subarray(matrix, 0, wordSize);
            }
        } catch (IOException e) {
            logger.error("词向量加载出错:{}", e);
            throw new IOException("词向量加载出错");
        }
    }

    public int getArraySize() {
        return arraySize;
    }

    public int getNumWords() {
        return wordSize;
    }

    public String getWord(int idx) {
        return vocab[idx];
    }

    public float getMatrixElement(int row, int column) {
        return matrix[row][column];
    }

    public int getWordSize() {
        return wordSize;
    }

    String[] getVocab() {
        return vocab;
    }

    float[][] getMatrix() {
        return matrix;
    }

    public String getFile() {
        return file;
    }
}
