package gg.kuken.features.account

import gg.kuken.features.account.repository.AccountRepository
import gg.kuken.features.account.repository.AccountsRepositoryImpl
import org.koin.dsl.module

val AccountDI = module {
    single<AccountRepository> {
        AccountsRepositoryImpl(database = get())
    }
    single<AccountService> {
        AccountServiceImpl(
            identityGeneratorService = get(),
            accountsRepository = get(),
            hashAlgorithm = get(),
            eventDispatcher = get()
        )
    }
}