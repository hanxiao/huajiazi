package com.ojins.chatbot;

import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import com.ojins.chatbot.seq2seq.EncodedQASet;
import com.ojins.chatbot.seq2seq.QAIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * ___   ___  ________  ___   __      __     __   ________ ________  ______
 * /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 * \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 * \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 * \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 * \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 * \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/
 * <p>
 * <p>
 * <p>
 * Created on 11/23/16.
 */
public class TestSeq2Seq {
    @Test
    public void testToyTraining() {
        Set<QAPair> qaStates = new HashSet<>();
        qaStates.add(new QAPairBuilder().setQuestion("苹果好").setAnswer("橘子不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("橘子好").setAnswer("苹果不好").build());
        qaStates.add(new QAPairBuilder().setQuestion("苹果不好").setAnswer("橘子好").build());

        EncodedQASet encodedQASet = new EncodedQASet(qaStates);

        final int seed = 1234;

        final int FEATURE_VEC_SIZE = encodedQASet.getVocabularySize();

        //Tweak these to tune - dataset size = batchSize * totalBatches
        final int batchSize = 10;
        final int totalBatches = 2;
        final int nEpochs = 2;
        final int nIterations = 1;
        final int numHiddenNodes = 128;

        DataTypeUtil.setDTypeForContext(DataBuffer.Type.DOUBLE);
        //Training data iterator
        QAIterator iterator = new QAIterator(seed, batchSize, totalBatches, encodedQASet);

        ComputationGraphConfiguration configuration = new NeuralNetConfiguration.Builder()
                //.regularization(true).l2(0.000005)
                .weightInit(WeightInit.XAVIER)
                .learningRate(0.5)
                .updater(Updater.RMSPROP)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(nIterations)
                .seed(seed)
                .graphBuilder()
                .addInputs("additionIn", "sumOut")
                .setInputTypes(InputType.recurrent(FEATURE_VEC_SIZE), InputType.recurrent(FEATURE_VEC_SIZE))
                .addLayer("encoder", new GravesLSTM.Builder().nIn(FEATURE_VEC_SIZE).nOut(numHiddenNodes).activation("softsign").build(), "additionIn")
                .addVertex("lastTimeStep", new LastTimeStepVertex("additionIn"), "encoder")
                .addVertex("duplicateTimeStep", new DuplicateToTimeSeriesVertex("sumOut"), "lastTimeStep")
                .addLayer("decoder", new GravesLSTM.Builder().nIn(FEATURE_VEC_SIZE + numHiddenNodes).nOut(numHiddenNodes).activation("softsign").build(), "sumOut", "duplicateTimeStep")
                .addLayer("output", new RnnOutputLayer.Builder().nIn(numHiddenNodes).nOut(FEATURE_VEC_SIZE).activation("softmax").lossFunction(LossFunctions.LossFunction.MCXENT).build(), "decoder")
                .setOutputs("output")
                .pretrain(false).backprop(true)
                .build();

        ComputationGraph net = new ComputationGraph(configuration);
        net.init();
        //net.setListeners(new ScoreIterationListener(200),new HistogramIterationListener(200));
        net.setListeners(new ScoreIterationListener(1));
        //net.setListeners(new HistogramIterationListener(200));
        //Train model:
        int iEpoch = 0;
        int testSize = 4;
        while (iEpoch < nEpochs) {
            System.out.printf("* = * = * = * = * = * = * = * = * = ** EPOCH %d ** = * = * = * = * = * = * = * = * = * = * = * = * = * =\n", iEpoch);
            net.fit(iterator);

            MultiDataSet testData = iterator.generateTest(testSize);
            ArrayList<int[]> testNums = iterator.testFeatures();
            INDArray[] prediction_array = net.output(testData.getFeatures(0), testData.getFeatures(1));
            INDArray predictions = prediction_array[0];
            INDArray answers = Nd4j.argMax(predictions, 1);
//
//            encode_decode(testnum1,testnum2,testSums,answers);

            iterator.reset();
            iEpoch++;
        }
        System.out.printf("\n* = * = * = * = * = * = * = * = * = ** EPOCH COMPLETE ** = * = * = * = * = * = * = * = * = * = * = * = * = * =\n", iEpoch);

    }
}
