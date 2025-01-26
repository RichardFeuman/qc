  // принцип открытости закрытости -сущности (классы, модули, функции и т.д.)
  // должны быть открыты для расширения и закрыты для изменений

  // пример кода, который соответсует OCP

  trait Model {

    val modelName: String
    def train(): Unit
    def test(): Unit
    def predict(): Unit

  }

  // реализация абстракции

  class RandomForestClassifier extends Model {

    override val modelName: String = "random forest classifier"

    override def train() = println(s"train $modelName")

    override def test() = println(s"test $modelName")

    override def predict(): Unit = println(s"predict based on $modelName")


   }

  trait ModelInitializer {

    def initializeModel(): Model
  }

  object RandomForestInitializer extends ModelInitializer {

    override def initializeModel(): Model = new RandomForestClassifier()

  }

  // service

  class MLService(modelInitializer: ModelInitializer) {

    def train(): Unit = {
        modelInitializer.initializeModel().train()
    }

    def test(): Unit = {
      modelInitializer.initializeModel().test()
    }

    def predict(): Unit = {
      modelInitializer.initializeModel().predict()
    }

  }

  // example of usage

  val mlService2 = new MLService(RandomForestInitializer)
  mlService2.test()
  mlService2.train()
  mlService2.predict()
