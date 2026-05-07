import com.vayikra.db.Users
import com.vayikra.models.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID
import kotlin.time.Clock

class UserService {
    fun findByEmail(email: String): User?  = transaction {
        Users.selectAll()
            .where { Users.email eq email }
            .map { row ->
                User(
                    id = row[Users.id],
                    email = row[Users.email],
                    name = row[Users.name],
                    city = row[Users.city],
                    country = row[Users.country]
                )
            }
            .singleOrNull()
    }

    fun findPasswordHash(email: String): String? = transaction {
        Users.selectAll()
            .where { Users.email eq email }
            .map { it[Users.passwordHash] }
            .singleOrNull()
    }

    fun createUser(email: String, password: String, name: String, city: String, country: String): User {
        val id = UUID.randomUUID().toString()
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        val now = Clock.System.now()

        transaction {
            Users.insert {
                it[Users.id] = id
                it[Users.email] = email
                it[Users.passwordHash] = hash
                it[Users.name] = name
                it[Users.city] = city
                it[Users.country] = country
                it[Users.createdAt] = now
            }
        }

        return User(id = id, email = email, name = name, city = city, country = country)
    }
}