package no.westerdals.auth

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.westerdals.auth.db.UserRepository
import no.westerdals.auth.dto.UserDTO
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(AuthApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(initializers = [(UserApiTests.Companion.Initializer::class)])
class UserApiTests {

    @LocalServerPort
    protected var port = 0
    private var path = "/api"

    @Autowired
    private lateinit var repository: UserRepository

    private val adminId = "admin"
    private val adminPass = "admin"
    private val userId = "foo"
    private val userPass = "bar"
/*
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
*//*
    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @ClassRule
        @JvmField
        val redis = KGenericContainer("redis:latest")
                .withExposedPorts(6379)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                val host = redis.containerIpAddress
                val port = redis.getMappedPort(6379)

                TestPropertyValues
                        .of("spring.redis.host=$host", "spring.redis.port=$port")
                        .applyTo(configurableApplicationContext.environment)
            }
        }
    }
*/
    @Before
    @After
    fun clean() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = path
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        repository.run{
            deleteAll()
            //we use these users for some tests
            createUser("foo", "bar")
            createUser("bar", "foo")
            createUser("user", "1234")
        }
    }

    /**
     *   Utility function used to create a new user in the database
     *//*
    private fun registerUser(id: String, password: String): String {


        return RestAssured.given().contentType(ContentType.JSON)
                .body(UserDTO(id, password))
                .post("/signUp")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .extract().cookie("SESSION")

        /*
            From now on, the user is authenticated.
            I do not need to use userid/password in the following requests.
            But each further request will need to have the SESSION cookie.
         */
    }

    private fun checkAuthenticatedCookie(cookie: String, expectedCode: Int){
        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedCode)
    }*/
/*

    @Test
    fun testLogin() {

        val name = "fool"
        val pwd = "barn"

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = registerUser(name, pwd)

        RestAssured.given().get("/testUser")
                .then()
                .statusCode(401)

        RestAssured.given().cookie("SESSION", cookie)
                .get("/testUser")
                .then()
                .statusCode(200)
                .body("name", equalTo(name))
                .body("roles", Matchers.contains("ROLE_USER"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = RestAssured.given().auth().basic(name, pwd)
                .get("/testUser")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("name", equalTo(name))
                .body("roles", Matchers.contains("ROLE_USER"))
                .extract().cookie("SESSION")

        Assert.assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = RestAssured.given().contentType(ContentType.JSON)
                .body(UserDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        Assert.assertNotEquals(login, cookie)
        Assert.assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }



    @Test
    fun testWrongLogin() {

        val name = "fool"
        val pwd = "barn"

        val noAuth = RestAssured.given().contentType(ContentType.JSON)
                .body(UserDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(noAuth, 401)

        registerUser(name, pwd)

        val auth = RestAssured.given().contentType(ContentType.JSON)
                .body(UserDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }*/

    @Test
    fun testGetAll() {

        RestAssured.given().accept(ContentType.JSON)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(3))
    }

    @Test
    fun testNotFoundUser() {

        RestAssured.given().accept(ContentType.JSON)
                .get("/users/notAUsernameThatExistsInDatabase")
                .then()
                .statusCode(404)
                .body("code", CoreMatchers.equalTo(404))
                .body("message", CoreMatchers.not(CoreMatchers.equalTo(null)))
    }

    @Test
    fun testRetrieveEachSingleUser() {

        val users = RestAssured.given().accept(ContentType.JSON)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(3))
                .extract().body().jsonPath().getList("data", UserDTO::class.java)

        for (u in users) {

            RestAssured.given().accept(ContentType.JSON)
                    .get("/users/${u.username}")
                    .then()
                    .statusCode(200)
                    .body("data.username", CoreMatchers.equalTo(u.username))
                    //remember we need to change to hashed password and
                    //use matched when we integrate postgres
                    //dbPassword = load hashed password from db
                    //.body("data.password", CoreMatchers.equalTo(passwordEncoder.matches(password, dbPassword)))
                    .body("data.password", CoreMatchers.equalTo(u.password))
                    .body("data.roles[0]", CoreMatchers.equalTo("USER"))
        }
    }

    @Test
    fun testCreateAndGet() {

        val username = "foop"
        val password = "bart"

        val dto = UserDTO(username = username, password = password)

        //Should have 3 users
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(3))

        //Creating a user
        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(dto)
                .post("/users")
                .then()
                .statusCode(201)

        /*
            later we have to extract the hashed password from the get to compare it to the non-crypted
            password. Will do this when we setup postgres
          */
        //Should be 4 user now
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(4))


        //One ticket with same data as the POST
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .pathParam("username", username)
                .get("/users/{username}")
                .then()
                .statusCode(200)
                .body("data.username", CoreMatchers.equalTo(username))
                //dbPassword = load hashed password from db
                //.body("data.password", CoreMatchers.equalTo(passwordEncoder.matches(password, dbPassword)))
                .body("data.password", CoreMatchers.equalTo(password))
                .body("data.roles[0]", CoreMatchers.equalTo("USER"))
    }

    @Test
    fun testDeleteAllUsers() {

        val users = RestAssured.given().accept(ContentType.JSON)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(3))
                .extract().body().jsonPath().getList("data", UserDTO::class.java)

        for (u in users) {

            RestAssured.given().accept(ContentType.JSON)
                    .delete("/users/${u.username}")
                    .then()
                    .statusCode(204)
        }

        RestAssured.given().accept(ContentType.JSON)
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testNotAuthenticated() {

        RestAssured.given().get("/testUser")
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", CoreMatchers.containsString("Basic realm=\"Realm\""))
    }

    @Test
    fun testNotAuthorizedUser() {

        RestAssured.given()
                .auth().basic(userId, userPass)
                .get("/testAdmin")
                .then()
                .statusCode(403)
    }

    @Test
    fun testAuthorizedUser() {

        //as we are currently just using the in memory auth from httpbasic
        //we have to use one of those 'users' and not the ones created in this test
        RestAssured.given()
                .auth().basic(userId, userPass)
                .get("/testUser")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAuthenticatedAdmin() {

        //as we are currently just using the in memory auth from httpbasic
        //we have to use one of those 'users' and not the ones created in this test
        RestAssured.given()
                .auth().basic(adminId, adminPass)
                .get("/testAdmin")
                .then()
                .statusCode(200)
    }

    @Test
    fun testCorrectXValue(){

        val x = "1"

        RestAssured.given()
                .auth().basic(adminId, adminId)
                .param("x", x)
                .get("/test")
                .then()
                .statusCode(200)
    }

    @Test
    fun testWrongXValue(){

        val x = "2"

        RestAssured.given()
                .auth().basic(adminId, adminId)
                .param("x", x)
                .get("/test")
                .then()
                .statusCode(400)
    }
}
