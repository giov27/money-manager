package controllers.helpers

import org.mindrot.jbcrypt.BCrypt
import sun.security.util.Password

import javax.inject.Singleton
import scala.util.Try

@Singleton
class Cryptic {
  def createPassword(password: String) : String = {
    try{
      if (password == null){
        throw new Exception("Empty Password")
      }
      BCrypt.hashpw(password, BCrypt.gensalt())
    }
    catch{
      case e: Exception => e.getMessage
    }
  }

  def checkPassword(password: String, passwordEncrypted: String): Boolean = {
    var result: Boolean = false
    if(password != null && passwordEncrypted != null){
       result = BCrypt.checkpw(password, passwordEncrypted)
    }
    result
  }
}
