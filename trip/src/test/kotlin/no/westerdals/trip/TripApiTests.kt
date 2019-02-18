package no.westerdals.trip

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.westerdals.trip.DTO.TripDTO
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(TripApplication::class)],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TripApiTests {

    @LocalServerPort
    protected var port = 0

    @Before
    @After
    fun clean() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/trip"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        /*
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */
        val list = RestAssured.given().accept(ContentType.JSON).get()
            .then()
            .statusCode(200)
            .extract()
            .`as`(Array<TripDTO>::class.java)
            .toList()


        /*
            Code 204: "No Content". The server has successfully processed the request,
            but the return HTTP response will have no body.
         */
        list.stream().forEach {
            RestAssured.given().pathParam("tripId", it.id)
                .delete("/{tripId}")
                .then()
                .statusCode(204)
        }

        RestAssured.given().get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))
    }

    private fun createTrip(): TripDTO {

        val name = "Travel Air"
        val location = "Rome, Italy"
        val price = 600
        val soldOut = false

        return TripDTO(name, location, price, soldOut)
    }

    @Test
    fun testCreateAndGet() {

        val dto = createTrip()

        //Should be no tickets
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))

        //Creating a ticket
        val id = RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(201)
            .extract().asString()

        //Should be 1 ticket now
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(1))

        //1 ticket with same data as the POST
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .get("/{tripId}")
            .then()
            .statusCode(200)
            .body("tripId", CoreMatchers.equalTo(id))
            .body("name", CoreMatchers.equalTo(dto.name))
            .body("location", CoreMatchers.equalTo(dto.location))
            .body("price", CoreMatchers.equalTo(dto.price))
            .body("soldOut", CoreMatchers.equalTo(dto.soldOut))
    }

    @Test
    fun testInvalidPostId() {

        val dto = createTrip()
        dto.id = "1"

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(400)
    }

    @Test
    fun testNullPost() {

        val dto = createTrip()

        dto.name = null

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(400)
    }

    @Test
    fun testInvalidGetId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", "Invalid")
            .get("/{tripId}")
            .then()
            .statusCode(404)
    }

    @Test
    fun testNonExistingGetId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", 1)
            .get("/{tripId}")
            .then()
            .statusCode(404)
    }

    @Test
    fun testUpdateTrip() {

        val dto = createTrip()
        val name = "Travel Agency"

        val id = RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(201)
            .extract().asString()

        val updatedName = "New Travel Agency"

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .body(TripDTO(
                name = name,
                location = dto.location,
                price = dto.price,
                soldOut = dto.soldOut,
                id = dto.id
                ))
            .put("/{tripId}")
            .then()
            .statusCode(204)

        //Check if updated correctly
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .get("/{tripId}")
            .then()
            .statusCode(200)
            .body("tripId", CoreMatchers.equalTo(id))
            .body("name", CoreMatchers.equalTo(updatedName))
            .body("location", CoreMatchers.equalTo(dto.location))
            .body("price", CoreMatchers.equalTo(dto.price))
            .body("soldOut", CoreMatchers.equalTo(dto.soldOut))
    }

    @Test
    fun testInvalidPutId() {

        val dto = createTrip()

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", 1)
            .body(TripDTO(
                name = dto.name,
                location = dto.location,
                price = dto.price,
                soldOut = dto.soldOut,
                id = null))
            .put("/{tripId}")
            .then()
            .statusCode(404)
    }

    @Test
    fun testMismatchPutId() {

        val dto = createTrip()

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", 1)
            .body(TripDTO(
                name = dto.name,
                location = dto.location,
                price = dto.price,
                soldOut = dto.soldOut,
                id = "2"))
            .put("/{tripId}")
            .then()
            .statusCode(409)
    }

    @Test
    fun testNonExistingPutId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))

        val dto = createTrip()

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", 1)
            .body(TripDTO(
                name = dto.name,
                location = dto.location,
                price = dto.price,
                soldOut = dto.soldOut,
                id = dto.id))
            .put("/{tripId}")
            .then()
            .statusCode(404)
    }

    @Test
    fun testNullPut() {

        val dto = createTrip()

        val id = RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(201)
            .extract().asString()

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .body(TripDTO(
                name = null,
                location = null,
                price = null,
                soldOut = null,
                id = dto.id))
            .put("/{tripId}")
            .then()
            .statusCode(400)
    }

    @Test
    fun testUpdatePrice() {

        val dto = createTrip()
        val price = 5000

        //Creating a ticket
        val id = RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(201)
            .extract().asString()

        val updatedPrice = 4000

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .param("price", price)
            .patch("/{tripId}/price")
            .then()
            .statusCode(204)

        //Check if updated correctly
        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", id)
            .get("/{tripId}")
            .then()
            .statusCode(200)
            .body("tripId", CoreMatchers.equalTo(id))
            .body("name", CoreMatchers.equalTo(dto.name))
            .body("location", CoreMatchers.equalTo(dto.location))
            .body("price", CoreMatchers.equalTo(updatedPrice))
            .body("soldOut", CoreMatchers.equalTo(dto.soldOut))
    }

    @Test
    fun testInvalidPatchId() {

        val price = 3000

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", "random")
            .param("price", price)
            .patch("/{tripId}/price")
            .then()
            .statusCode(400)
    }

    @Test
    fun testNonExistingPatchId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))

        val price = 2000

        RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("tripId", 1)
            .param("price", price)
            .patch("/{tripId}/price")
            .then()
            .statusCode(404)
    }

    @Test
    fun testDeleteTicket() {

        val dto = createTrip()

        val id = RestAssured.given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(dto)
            .post()
            .then()
            .statusCode(201)
            .extract().asString()

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(1))

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .delete("/${id}")
            .then()
            .statusCode(204)

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testInvalidDeleteId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .delete("Invalid")
            .then()
            .statusCode(400)
    }

    @Test
    fun testNonExistingDeleteId() {

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .get()
            .then()
            .statusCode(200)
            .body("size()", CoreMatchers.equalTo(0))

        RestAssured.given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .delete("1")
            .then()
            .statusCode(404)
    }
}