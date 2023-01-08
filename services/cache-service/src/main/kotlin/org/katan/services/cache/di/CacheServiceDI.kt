package org.katan.services.cache.di

import org.katan.services.cache.CacheService
import org.katan.services.cache.RedisCacheServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val cacheServiceDI: Module = module {
    single<CacheService> { RedisCacheServiceImpl(config = get()) }
}
