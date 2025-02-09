

/*
  Для реализации Cake паттерна потребуется:

  1. зависимости (передаваемые классу в качестве параметра) реализовать через self типы
  2. Db, AppService обернуть в трейты - это будут компоненты приложения
  3. внутри компонент прописать переменные, значения которых будут задаваться при подмиксовывании компонент
  4. создать отдельный объект, в котором будет происходить присваивание переменным соответствующих значений (будут создаваться необходимые экземпляры классов)

   */


  val envr = "production"
  val userType = "registered"
  val date = "2024-12-27"
  val username = "santa"
  val age = 50

  trait User {
    val username: String
    val age: Int
    val visitDate: String
  }

  case class RegisteredUser(
                             username: String,
                             age: Int,
                             visitDate: String)
    extends User

  case class AnonymousUser(visitDate: String) extends User {
    override val username: String = "anonymous_user"
    override val age: Int = -1
  }

  trait Configuration {
    def env: String
  }

  class ProductionConfiguration extends Configuration {
    val env = "production"
  }
  class TestingConfiguration extends Configuration {
    val env = "test"
  }

  // rf step 1
  trait UserComponent {

    val user: User

  }

  // rf step 1
  trait ConfigurationComponent {

    val config: Configuration

  }

  // rf step 2

  trait UserMonitoringComponent {

    self: UserComponent =>

    val userMonitoringService : MonitoringService


    class MonitoringService {
      def monitor(): Unit = {
        val userType = self.user match {
          case RegisteredUser(username, age, _) if age < 18 ⇒ s"User [$username] under 18 logged in"
          case RegisteredUser(username, _, _) ⇒ s"Adult user [$username] logged in"
          case AnonymousUser(date) ⇒ s"Anonymous user came at $date"
        }

        println(userType)
      }
    }

  }

  // rf step 3

  trait AppComponents extends UserComponent with ConfigurationComponent with UserMonitoringComponent

  // rf step 4



  object UserApp extends AppComponents  {

    override val user: User =
      userType match {
          case "registered" => new RegisteredUser(username, age, date)
          case "anonymous" => new AnonymousUser(date)
      }

    override val config: Configuration = envr match {
            case "production" => new ProductionConfiguration
            case "test" => new TestingConfiguration
    }

    override val userMonitoringService: UserApp.MonitoringService = new MonitoringService

    def run(): Unit = {
      println(s"run in [${config.env}] env")
      userMonitoringService.monitor()
    }

  }


  import UserApp._

  UserApp.run()
