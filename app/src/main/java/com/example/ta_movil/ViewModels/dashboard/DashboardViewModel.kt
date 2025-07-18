package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// Modelo de transacción
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: String,
    val paymentMethod: String = ""
)

data class SavingGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double
)

enum class Screen {
    Dashboard,
    IngresosEgresos,
    Historial,
    Configuracion
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

open class DashboardViewModel : ViewModel() {
    // Estado para las metas de ahorro
    var savingGoals by mutableStateOf<List<SavingGoal>>(emptyList())
        private set

    // Estado para las transacciones
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    // Estado para la pantalla actual del BottomNavigationBar
    var currentScreen by mutableStateOf(Screen.Dashboard)
        private set

    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set

    // Estado de error
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Inicializar Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        // No cargar datos al inicio, esperamos que el usuario se autentique primero
    }

    // Para fines de preview/test
    fun setFakeGoals(goals: List<SavingGoal>) {
        savingGoals = goals
    }

    fun loadSavingGoals() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        // Primero obtener la referencia al documento del usuario
        val userRef = db.collection("users").document(currentUser.uid)

        // Luego obtener las metas de ahorro
        userRef.collection("savingGoals")
            .get()
            .addOnSuccessListener { documents ->
                savingGoals = documents.map { document ->
                    SavingGoal(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        targetAmount = document.getDouble("targetAmount") ?: 0.0,
                        currentAmount = document.getDouble("currentAmount") ?: 0.0
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar las metas de ahorro: ${e.message}"
                isLoading = false
            }
    }

    fun loadTransactions() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                transactions = documents.map { document ->
                    Transaction(
                        id = document.id,
                        type = when (document.getString("type")) {
                            "INCOME" -> TransactionType.INCOME
                            "EXPENSE" -> TransactionType.EXPENSE
                            else -> TransactionType.EXPENSE
                        },
                        amount = document.getDouble("amount") ?: 0.0,
                        description = document.getString("description") ?: "",
                        date = document.getString("date") ?: "",
                        paymentMethod = document.getString("paymentMethod") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar las transacciones: ${e.message}"
                isLoading = false
            }
    }

    // CORREGIDO: Método addTransaction con callbacks
    fun addTransaction(
        transaction: Transaction,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val error = "No hay usuario autenticado"
            errorMessage = error
            onFailure(error)
            return
        }

        // Convertir Transaction a Map para Firestore
        val transactionMap = hashMapOf(
            "id" to transaction.id,
            "type" to transaction.type.name, // Convertir enum a string
            "amount" to transaction.amount,
            "description" to transaction.description,
            "date" to transaction.date,
            "paymentMethod" to transaction.paymentMethod,
            "timestamp" to System.currentTimeMillis() // Para ordenar por fecha de creación
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .document(transaction.id)
            .set(transactionMap)
            .addOnSuccessListener {
                // Agregar inmediatamente a la lista local
                transactions = listOf(transaction) + transactions
                errorMessage = null

                // Recargar las transacciones para mantener sincronización
                loadTransactions()

                // Ejecutar callback de éxito
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val error = "Error al agregar la transacción: ${exception.message}"
                errorMessage = error
                onFailure(error)
            }
    }

    // CORREGIDO: Método deleteTransaction con callbacks
    fun deleteTransaction(
        transactionId: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val error = "No hay usuario autenticado"
            errorMessage = error
            onFailure(error)
            return
        }

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                // Eliminar inmediatamente de la lista local
                transactions = transactions.filter { it.id != transactionId }
                errorMessage = null

                // Recargar las transacciones para mantener sincronización
                loadTransactions()

                // Ejecutar callback de éxito
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val error = "Error al eliminar la transacción: ${exception.message}"
                errorMessage = error
                onFailure(error)
            }
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun updateSavingGoal(goal: SavingGoal) {
        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("savingGoals")
            .document(goal.id)
            .set(goal)
            .addOnSuccessListener {
                loadSavingGoals()
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al actualizar la meta: ${exception.message}"
            }
    }

    fun updateTransaction(transaction: Transaction) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        val transactionMap = hashMapOf(
            "id" to transaction.id,
            "type" to transaction.type.name,
            "amount" to transaction.amount,
            "description" to transaction.description,
            "date" to transaction.date,
            "paymentMethod" to transaction.paymentMethod,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .document(transaction.id)
            .set(transactionMap)
            .addOnSuccessListener {
                loadTransactions()
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al actualizar la transacción: ${exception.message}"
            }
    }


        fun deleteSavingGoal(goalId: String) {
            db.collection("users")
                .document(auth.currentUser?.uid ?: "")
                .collection("savingGoals")
                .document(goalId)
                .delete()
                .addOnSuccessListener {
                    loadSavingGoals()
                }
                .addOnFailureListener { exception ->
                    errorMessage = "Error al eliminar la meta: ${exception.message}"
                }
        }

        // AGREGADO: Método para limpiar errores
        fun clearError() {
            errorMessage = null
        }

}