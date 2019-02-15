@SpringBootApplication(scanBasePackages = ["soloexam"])
@EnableDiscoveryClient
class GatewayApplication{
    @Bean
    fun corsConfiguration(routePredicateHandlerMapping: RoutePredicateHandlerMapping): CorsConfiguration {
        val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST).forEach {
            m -> corsConfiguration.addAllowedMethod(m) }
        corsConfiguration.addAllowedOrigin("*")
        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedHeader("*")
        routePredicateHandlerMapping.setCorsConfigurations(object : HashMap<String, CorsConfiguration>() {
            init {
                put("/**", corsConfiguration)
            }
        })
        return corsConfiguration
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, *args)
}