package no.westerdals.auth

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.westerdals.auth.dto.TestDto
import no.westerdals.auth.util.WrappedResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping(path = ["/api"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@Validated
class TestApi {


    @ApiOperation("Simple test")
    @GetMapping(path = ["/test"])
    fun test(
            @ApiParam("Has to be 1")
            @RequestParam("x", required = true)
            x: Int

    ): ResponseEntity<WrappedResponse<TestDto>> {

        if (x == 1) {
            return ResponseEntity.status(200).body(
                    WrappedResponse<TestDto>(
                            code = 200,
                            message = "Correct value")
                            .validated()
            )
        }
        else {
            return ResponseEntity.status(400).body(
                    WrappedResponse<TestDto>(
                            code = 400,
                            message = "Needs to be 1")
                            .validated()
            )
        }
    }

    @GetMapping(produces = [(MediaType.TEXT_PLAIN_VALUE)],
            path = ["/testUser"])
    fun forUsers() : String{
        return "User auth or higher can access"
    }

    @GetMapping(produces = [(MediaType.TEXT_PLAIN_VALUE)],
            path = ["/testAdmin"])
    fun forAdmins() : String{
        return "Admin only can access"
    }
}
