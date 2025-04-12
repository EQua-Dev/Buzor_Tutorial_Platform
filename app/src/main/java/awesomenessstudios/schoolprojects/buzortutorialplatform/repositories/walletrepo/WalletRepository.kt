package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet

interface WalletRepository {
    suspend fun getWalletByUserId(userId: String): Wallet?
    suspend fun debitWallet(
        userId: String,
        amount: Double,
        description: String,
        location: String,
        receiver: String
    ): Result<Unit>

    suspend fun creditWallet(
        userId: String,
        amount: Double,
        description: String,
        sender: String,
    ): Result<Unit>

}