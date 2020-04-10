package com.rmarioo.repeatableexecutrion.rest.config

import com.rmarioo.repeatableexecutrion.core.model.RealClock
import com.rmarioo.repeatableexecutrion.core.SearchUseCase
import com.rmarioo.repeatableexecutrion.core.model.Clock
import com.rmarioo.repeatableexecutrion.supplierAdapter.RealSupplierRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchConfiguration{

    @Bean
    fun searchUseCase(): SearchUseCase {

        return SearchUseCaseFactory()
            .create("http://aSupplierUrl")
    }

    class SearchUseCaseFactory(val clock: Clock = RealClock() )
    {
        fun create(supplierUrl: String): SearchUseCase {
            return SearchUseCase(
                clock,
                RealSupplierRepository(
                    url = supplierUrl
                )
            )
        }
    }
}
