/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package learndl4kotlin

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Nadam
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction
import org.slf4j.LoggerFactory

object App {

    private val log = LoggerFactory.getLogger(App::class.java)

    private const val numRows = 28
    private const val numColumns = 28
    private const val outputNum = 10 // number of output classes
    private const val batchSize = 128 // batch size for each epoch
    private const val rngSeed = 123 // random number seed for reproducibility
    private const val numEpochs = 15 // number of epochs to perform

    fun doSingleLayer() {
        //Get the DataSetIterators:
        val mnistTrain = MnistDataSetIterator(batchSize, true, rngSeed)
        val mnistTest = MnistDataSetIterator(batchSize, false, rngSeed)

        log.info("Build model....")
        val conf = NeuralNetConfiguration.Builder()
                .seed(rngSeed.toLong()) //include a random seed for reproducibility
                // use stochastic gradient descent as an optimization algorithm
                .updater(Nadam()) //specify the rate of change of the learning rate.
                .l2(1e-4)
                .list()
                .layer(DenseLayer.Builder() //create the first, input layer with xavier initialization
                        .nIn(numRows * numColumns)
                        .nOut(1000)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                        .nIn(1000)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .build()

        val model = MultiLayerNetwork(conf)
        model.init()

        log.info("Train model....")
        model.setListeners(ScoreIterationListener(1), org.deeplearning4j.optimize.listeners.EvaluativeListener(mnistTest, 300)) //print the score with every 1 iteration and evaluate periodically
        model.fit(mnistTrain, numEpochs)

        log.info("Evaluate model....")
        val eval: org.nd4j.evaluation.classification.Evaluation = model.evaluate(mnistTest)
        log.info(eval.stats())

        log.info("****************Example finished********************")
    }
}

fun main() {
    App.doSingleLayer()
}
