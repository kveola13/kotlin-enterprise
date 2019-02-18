package no.westerdals.user

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.westerdals.user.DTO.UserDTO
import no.westerdals.user.repo.UserRepository
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner


@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(UserApplication::class)],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @LocalServerPort
    protected var port = 0
    private var path = "/api"

    @Autowired
    private lateinit var repository: UserRepository

    @Before
    @After
    fun clean() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = path
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        repository.deleteAll()
    }

    @Test
    fun testNeedAdmin(){
        RestAssured.given()
            .then()
            .statusCode(401)
    }

    @Test
    fun testOpenCount() {

        val count = RestAssured.given()
            .get("/userDetailsCount")
            .then()
            .statusCode(200)
            .extract().body().asString().toInt()

        Assert.assertEquals(0, count)
    }

    @Test
    fun testGetAllWithAdmin(){

        checkSize(0)
    }

    private fun checkSize(n: Int){
        RestAssured.given().auth().basic("admin", "admin")
            .accept(ContentType.JSON)
            .get("/userDetails")
            .then()
            .statusCode(200)
            .body("size()", equalTo(n))
    }

    @Test
    fun testCreateAndGet(){

        checkSize(0)

        val id = "foo"

        val dto = UserDTO(id, "name", "surname", "e@email.com", 18, null)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(201)

        checkSize(1)
    }

    @Test
    fun testGetIdNotFound(){

        checkSize(0)

        val id = "foo"

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .get("/userDetails/thisIsNotAUserNameThatExists")
            .then()
            .statusCode(404)
    }

    @Test
    fun testChangeField(){

        checkSize(0)

        val id = "foo"
        val name = "bar"

        val dto = UserDTO(id, name, "bar", "e@email.com", 21, null)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(201)

        val changed = name + "_change"

        dto.name = changed

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(204)

        checkSize(1)

        RestAssured.given().auth().basic(id, "bar")
            .accept(ContentType.JSON)
            .get("userDetails/$id")
            .then()
            .statusCode(200)
            .body("name", equalTo(changed))
    }

    @Test
    fun testPartialUpdate(){

        checkSize(0)

        val id = "foo"
        val email = "e@email.com"

        val dto = UserDTO(id, "bar", "bar", email, 21, null)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(201)

        val changed =  "chang" + email

        dto.email = changed

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .param("email", changed)
            .patch("/userDetails/$id")
            .then()
            .statusCode(204)

        checkSize(1)

        RestAssured.given().auth().basic(id, "bar")
            .accept(ContentType.JSON)
            .get("userDetails/$id")
            .then()
            .statusCode(200)
            .body("email", equalTo(changed))
    }

    @Test
    fun testDeleteUser(){

        checkSize(0)

        val id = "foo"

        val dto = UserDTO(id, "name", "surname", "e@email.com", 18, null)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(201)

        checkSize(1)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .delete("/userDetails/$id")
            .then()
            .statusCode(204)

        checkSize(0)
    }

    @Test
    fun testDeleteUserNotExists(){
        checkSize(0)

        val id = "foo"

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .delete("/userDetails/$id")
            .then()
            .statusCode(404)

        checkSize(0)
    }

    @Test
    fun testUpdateEmailNoParam(){

        checkSize(0)

        val id = "foo"
        val email = "e@email.com"

        val dto = UserDTO(id, "bar", "bar", email, 21, null)

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .body(dto)
            .put("/userDetails/$id")
            .then()
            .statusCode(201)

        checkSize(1)

        val blank =  ""
        dto.email = blank

        RestAssured.given().auth().basic(id, "bar")
            .contentType(ContentType.JSON)
            .param("email", blank)
            .patch("/userDetails/$id")
            .then()
            .statusCode(400)
    }
}