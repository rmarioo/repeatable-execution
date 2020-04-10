package com.rmarioo.repeatableexecutrion.rest

import com.rmarioo.repeatableexecutrion.core.model.Clock
import com.rmarioo.repeatableexecutrion.rest.config.SearchConfiguration.SearchUseCaseFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import java.time.LocalDateTime
import java.time.LocalDateTime.of
import java.time.Month


class SearchControllerTest {


    companion object {
        lateinit var mvc: MockMvc

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {

            val searchUseCase = SearchUseCaseFactory(clock = FixedClock(of(2020, Month.JANUARY, 1, 10, 30, 0, 0)))
                .create("anyUrl")
            mvc = standaloneSetup(SearchController(searchUseCase)).build()
        }

    }

    class FixedClock(private val fixedDateTime: LocalDateTime) : Clock {
        override fun currentDateTime(): LocalDateTime = fixedDateTime
    }
    @Test
    internal fun searchOK() {

       mvc.perform(
            MockMvcRequestBuilders
                .post("/search")
                .content(
                       """{
                          "departureDate": "2020-04-12T13:23:34.070Z",
                          "arrivalDate": "2020-04-12T13:23:34.070Z",
                          "departureAirport": "MXP",
                          "arrivalAirport": "LON",
                          "adults": 1
                        }"""
                    )
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                )
            .andExpect(status().is2xxSuccessful)

    }

    @Test
    internal fun searchKO() {

       mvc.perform(
            MockMvcRequestBuilders
                .post("/search")
                .content(
                       """{
                          "departureDate": "2020-04-12T13:23:34.070Z",
                          "arrivalDate": "2020-04-12T13:23:34.070Z",
                          "departureAirport": "MXP",
                          "arrivalAirport": "LON",
                          "adults": 1,
                          "simulateServerError": true
                        }"""
                    )
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                )
            .andExpect(status().isInternalServerError)

    }

    @Test
    internal fun departureDateInPast() {
        mvc.perform(
            MockMvcRequestBuilders
                .post("/search")
                .content(
                    """{
                          "departureDate": "2020-04-12T13:23:34.070Z",
                          "arrivalDate": "2020-04-12T13:23:34.070Z",
                          "departureAirport": "MXP",
                          "arrivalAirport": "LON",
                          "adults": 1,
                          "simulateServerError": true
                        }"""
                )
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)

    }

}
