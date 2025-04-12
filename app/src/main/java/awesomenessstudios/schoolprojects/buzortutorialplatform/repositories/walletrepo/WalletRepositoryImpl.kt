package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo

import android.util.Log
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.WalletHistory
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.WALLETS_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WalletRepository {
    override suspend fun getWalletByUserId(userId: String): Wallet? {
        return try {
            val snapshot = firestore.collection(WALLETS_REF)
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject(Wallet::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun debitWallet(
        userId: String,
        amount: Double,
        description: String,
        location: String,
        receiver: String
    ): Result<Unit> {
        return try {
            val walletDoc = firestore.collection(WALLETS_REF)
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
                .documents.firstOrNull()
                ?: return Result.failure(Exception("Wallet not found"))

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(walletDoc.reference)
                val currentBalance = snapshot.getString("balance")?.toDouble() ?: 0.0
                if (currentBalance < amount) {
                    throw Exception("Insufficient funds")
                }
                transaction.update(
                    walletDoc.reference,
                    "balance",
                    (currentBalance - amount).toString()
                )
            }.await()

            // Add wallet history
            addWalletHistory(
                walletId = walletDoc.id,
                transactionType = "debit",
                amount = amount,
                description = description,
                location = location,
                receiver = receiver,
                sender = "N/A"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun creditWallet(
        userId: String,
        amount: Double,
        description: String,
        sender: String,

        ): Result<Unit> {
        return try {
            val walletDoc = firestore.collection(WALLETS_REF)
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
                .documents.firstOrNull()
                ?: return Result.failure(Exception("Wallet not found"))

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(walletDoc.reference)
                val currentBalance = snapshot.getString("balance")?.toDouble() ?: 0.0
                transaction.update(
                    walletDoc.reference,
                    "balance",
                    (currentBalance + amount).toString()
                )
            }.await()

            // Add wallet history
            addWalletHistory(
                walletId = walletDoc.id,
                transactionType = "credit",
                amount = amount,
                sender = sender,
                receiver = "N/A",
                description = description,
                location = "N/A"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun addWalletHistory(
        walletId: String,
        transactionType: String,
        amount: Double,
        sender: String,
        receiver: String,
        description: String,
        location: String
    ) {
        try {
            val history = WalletHistory(
                id = firestore.collection(WALLETS_REF).document().id,
                walletId = walletId,
                transactionType = transactionType,
                sender = sender,
                receiver = receiver,
                amount = amount,
                description = description,
                transactionLocation = location,
                dateCreated = System.currentTimeMillis().toString()
            )



            firestore.collection("wallets")
                .document(walletId)
                .collection("walletHistory")
                .add(history)
                .await()
        } catch (e: Exception) {
            // Log error but don't fail the whole operation
            Log.e("WalletRepository", "Error adding wallet history", e)
        }
    }
}