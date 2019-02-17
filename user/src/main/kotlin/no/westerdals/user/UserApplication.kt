package no.westerdals.user

@EnableSwagger2
@EnableDiscoveryClient
class UserApplication {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .paths(PathSelectors.any())
            .build()
    }
}
fun main(args: Array<String>) {
    SpringApplication.run(UserApplication::class.java, *args)
}