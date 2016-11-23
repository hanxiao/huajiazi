package com.ojins.chatbot.seq2seq;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;


/**
 * Created by susaneraly on 3/27/16.
 * This is class to generate pairs of random numbers given a maximum number of digits
 * This class can also be used as a reference for dataset iterators and writing one's own custom dataset iterator
 */

public class QAIterator implements MultiDataSetIterator {

    private Random randnumG;
    private int currentBatch;
    private int[] questionArr;
    private int[] answerArr;
    private boolean toTestSet;
    private final int seed;
    private final int batchSize;
    private final int totalBatches;
    private final int encoderSeqLength;
    private final int decoderSeqLength;
    private final int outputSeqLength;
    private final int timestep;
    private final EncodedQASet encodedQASet;

    private final int SEQ_VECTOR_DIM;

    public QAIterator(int seed, int batchSize, int totalBatches, EncodedQASet encodedQASet, int timestep) {

        this.seed = seed;
        this.randnumG = new Random(seed);

        this.batchSize = batchSize;
        this.totalBatches = totalBatches;

        this.encodedQASet = encodedQASet;
        this.timestep = timestep;

        this.SEQ_VECTOR_DIM = encodedQASet.getVocabularySize();
        this.encoderSeqLength = encodedQASet.maxQuestionLen;
        this.decoderSeqLength = encodedQASet.maxAnswerLen + 1;
        this.outputSeqLength = decoderSeqLength;

        this.currentBatch = 0;
    }

    public MultiDataSet generateTest(int testSize) {
        toTestSet = true;
        MultiDataSet testData = next(testSize);
        return testData;
    }

    public ArrayList<int[]> testFeatures() {
        ArrayList<int[]> testNums = new ArrayList<int[]>();
        testNums.add(questionArr);
        return testNums;
    }

    public int[] testLabels() {
        return answerArr;
    }

    @Override
    public MultiDataSet next(int sampleSize) {
        //Initialize everything with zeros - will eventually fill with one hot vectors
        INDArray encoderSeq = Nd4j.zeros(sampleSize, SEQ_VECTOR_DIM, encoderSeqLength);
        INDArray decoderSeq = Nd4j.zeros(sampleSize, SEQ_VECTOR_DIM, decoderSeqLength);
        INDArray outputSeq = Nd4j.zeros(sampleSize, SEQ_VECTOR_DIM, outputSeqLength);

        //Since these are fixed length sequences of timestep
        //Masks are not required
        INDArray encoderMask = Nd4j.ones(sampleSize, encoderSeqLength);
        INDArray decoderMask = Nd4j.ones(sampleSize, decoderSeqLength);
        INDArray outputMask = Nd4j.ones(sampleSize, outputSeqLength);

        if (toTestSet) {
            questionArr = new int[sampleSize];
            answerArr = new int[sampleSize];
        }

        /* ========================================================================== */
        IntStream.range(0, sampleSize).forEach(iSample -> {
            // sample a random idx from QA set
            int idx = randnumG.nextInt(encodedQASet.size());
            int[] sampleQ = encodedQASet.getAQuestion(idx);
            int[] sampleA = encodedQASet.getAnAnswer(idx);

            if (toTestSet) {
                questionArr[iSample] = idx;
                answerArr[iSample] = idx;
            }
            /*
            Encoder sequence:
            Eg. with numdigits=4, num1=123, num2=90
                123 + 90 is encoded as "   09+321"
                Converted to a string to a fixed size given by 2*numdigits + 1 (for operator)
                then reversed and then masked
                Reversing input gives significant gain
                Each character is transformed to a 12 dimensional one hot vector
                    (index 0-9 for corresponding digits, 10 for "+", 11 for " ")
            */
            int spaceFill = encoderSeqLength - sampleQ.length;
            int iPos = 0;
            //Fill in spaces, as necessary
            while (spaceFill > 0) {
                encoderSeq.putScalar(new int[]{iSample, encodedQASet.SPACE_OR_END, iPos}, 1);
                iPos++;
                spaceFill--;
            }

            //Fill in encoded question in REVERSE order
            for (int i = sampleQ.length - 1; i >= 0; i--) {
                encoderSeq.putScalar(new int[]{iSample, sampleQ[i], iPos}, 1);
                iPos++;
            }
            //Mask input for rest of the time series
            //while (iPos < timestep) {
            //    encoderMask.putScalar(new []{iSample,iPos},1);
            //    iPos++;
            // }
            //Fill in the digits from the sum
            iPos = 0;
            for (int c : sampleA) {
                outputSeq.putScalar(new int[]{iSample, c, iPos}, 1);
                //decoder input filled with spaces
                decoderSeq.putScalar(new int[]{iSample, encodedQASet.SPACE_OR_END, iPos}, 1);
                iPos++;
            }
            //Fill in spaces, as necessary
            //Leaves last index for "."
            while (iPos < decoderSeqLength) {
                //spaces encoded at index 12
                outputSeq.putScalar(new int[]{iSample, encodedQASet.SPACE_OR_END, iPos}, 1);
                //decoder input filled with spaces
                decoderSeq.putScalar(new int[]{iSample, encodedQASet.SPACE_OR_END, iPos}, 1);
                iPos++;
            }
        });
        //Predict "."
        /* ========================================================================== */
        INDArray[] inputs = new INDArray[]{encoderSeq, decoderSeq};
        INDArray[] inputMasks = new INDArray[]{encoderMask, decoderMask};
        INDArray[] labels = new INDArray[]{outputSeq};
        INDArray[] labelMasks = new INDArray[]{outputMask};
        currentBatch++;
        return new org.nd4j.linalg.dataset.MultiDataSet(inputs, labels, inputMasks, labelMasks);
    }

    @Override
    public void reset() {
        currentBatch = 0;
        toTestSet = false;
        randnumG = new Random(seed);
    }

    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public boolean hasNext() {
        //This generates numbers on the fly
        return currentBatch < totalBatches;
    }

    @Override
    public MultiDataSet next() {
        return next(batchSize);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }

    public void setPreProcessor(MultiDataSetPreProcessor multiDataSetPreProcessor) {

    }
}

