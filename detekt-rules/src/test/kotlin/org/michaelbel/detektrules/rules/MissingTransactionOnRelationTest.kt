package org.michaelbel.detektrules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MissingTransactionOnRelationTest {

    private val subject = MissingTransactionOnRelation(Config.empty)

    @Test
    fun `reports method returning Pojo type without Transaction`() {
        val code = """
            @Dao
            interface UserDao {
                @Query("SELECT * FROM users")
                suspend fun getUserWithPosts(): UserWithPostsPojo
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports method returning List of Pojo type without Transaction`() {
        val code = """
            @Dao
            interface UserDao {
                @Query("SELECT * FROM users")
                suspend fun getUsersWithPosts(): List<UserWithPostsPojo>
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `reports method returning Flow of Pojo type without Transaction`() {
        val code = """
            @Dao
            interface OrderDao {
                @Query("SELECT * FROM orders")
                fun getOrdersWithItems(): Flow<List<OrderWithItemsPojo>>
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }

    @Test
    fun `does not report when Transaction annotation is present`() {
        val code = """
            @Dao
            interface UserDao {
                @Transaction
                @Query("SELECT * FROM users")
                suspend fun getUsersWithPosts(): List<UserWithPostsPojo>
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report when return type has no Pojo postfix`() {
        val code = """
            @Dao
            interface UserDao {
                @Query("SELECT * FROM users")
                suspend fun getUsers(): List<User>
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `does not report non-Dao interface`() {
        val code = """
            interface UserRepository {
                suspend fun getUsersWithPosts(): List<UserWithPostsPojo>
            }
        """.trimIndent()

        assertEquals(emptyList<String>(), subject.lint(code).map { it.message })
    }

    @Test
    fun `reports correct message`() {
        val code = """
            @Dao
            interface UserDao {
                suspend fun getUserWithPosts(): UserWithPostsPojo
            }
        """.trimIndent()

        val expected = "Method 'getUserWithPosts' returns 'UserWithPostsPojo' (Pojo type). " +
            "Add @Transaction to ensure consistent reads."
        assertEquals(listOf(expected), subject.lint(code).map { it.message })
    }

    @Test
    fun `postfix check is case-insensitive`() {
        val code = """
            @Dao
            interface UserDao {
                suspend fun get(): UserPOJO
            }
        """.trimIndent()

        assertEquals(1, subject.lint(code).size)
    }
}
