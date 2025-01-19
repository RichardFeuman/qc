

  // my own example of code with di

  // entity
  case class TrainedModel( id: String, name: String, paramValueAfterTrain: Double )


  // abstract
  trait MLtrainer {

    def train(data: Vector[Double]) : TrainedModel

    def test(modelToTest: TrainedModel, testData: Vector[Double] = Vector.empty): TrainedModel

  }

  // class which inherits abstract

  class XGBoostTrainer extends MLtrainer {

    override def train(data: Vector[Double] = Vector.empty): TrainedModel = {
      TrainedModel("555", "XGBoostClassifier", data.sum/data.size)
    }

    override def test(modelToTest: TrainedModel, testData: Vector[Double] = Vector.empty): TrainedModel = {
      TrainedModel(modelToTest.id, modelToTest.name, testData.sum/testData.size)
    }

  }

  // service

  class ModelService(trainer: MLtrainer) {

    def trainModel(trainData: Vector[Double]) = {

      val trainedModel = trainer.train(trainData)

      trainedModel

    }

    def testModel(modelToTest: TrainedModel, testData: Vector[Double] = Vector.empty) = {

      val testedModel = trainer.test(modelToTest, testData)

      testedModel

    }

  }

  val mlService = new ModelService(new XGBoostTrainer)

  val trainedModel = mlService.trainModel(Vector.fill(10)(10.5))

  val testedModel = mlService.testModel(trainedModel, Vector.fill(10)(15.5))

  println(s"trainedModel score = ${trainedModel.paramValueAfterTrain}")
  println(s"testedModel score = ${testedModel.paramValueAfterTrain}")
