package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.WalletHistory

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

    fun observeWalletHistory(walletId: String, onUpdate: (List<WalletHistory>) -> Unit)
    suspend fun addToEscrow(
        amount: Double,
        studentWalletId: String,
        teacherWalletId: String,
        sessionType: String,
        sessionId: String,
        courseId: String
    ): Result<Unit>

    suspend fun releaseEscrowToTeacher(sessionId: String): Result<Unit>
}