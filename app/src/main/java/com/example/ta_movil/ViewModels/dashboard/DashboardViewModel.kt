package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.ta_movil.ViewModels.userLogin.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp

// Modelo de transacción
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: String,
    val paymentMethod: String = "",
    val categoryId: DocumentReference? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class SavingGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double
)

enum class Screen {
    Dashboard,
    Goals,
    Categorias,
    IngresosEgresos,
    Configuracion
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

class DashboardViewModel : ViewModel() {
    // Estado para las metas de ahorro
    var savingGoals by mutableStateOf<List<SavingGoal>>(emptyList())
        private set

    // Estado para las transacciones
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    // Estado para la pantalla actual del BottomNavigationBar
    private var _currentScreen by mutableStateOf(Screen.Dashboard)
    val currentScreen: Screen
        get() = _currentScreen

    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set

    // Estado de error
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Estado para el modal de metas
    var showAddGoalModal by mutableStateOf(false)
        private set

    // Estado para la meta que se está editando
    var currentEditingGoal by mutableStateOf<SavingGoal?>(null)
        private set

    // Estado para el diálogo de logout
    var showLogoutDialog by mutableStateOf(false)

    // Inicializar Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val authViewModel: AuthViewModel = AuthViewModel()

    init {
        // Cargar datos al inicializar si hay un usuario autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadSavingGoals()
            loadTransactions()
        }
    }

    fun loadSavingGoals() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        db.collection("users").document(currentUser.uid)
            .collection("savingGoals")
            .get()
            .addOnSuccessListener { documents ->
                val goals = documents.mapNotNull { document ->
                    try {
                        SavingGoal(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            targetAmount = document.getDouble("targetAmount") ?: 0.0,
                            currentAmount = document.getDouble("currentAmount") ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                savingGoals = goals
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
                val transactionList = documents.mapNotNull { document ->
                    try {
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
                            paymentMethod = document.getString("paymentMethod") ?: "",
                            categoryId = document.get("categoryId") as? DocumentReference,
                            timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
                        )


                    } catch (e: Exception) {
                        println("Error al parsear transacción: ${e.message}")
                        null
                    }
                }
                transactions = transactionList
                isLoading = false
                println("Transacciones cargadas: ${transactions.size}")
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al cargar las transacciones: ${e.message}"
                isLoading = false
                println("Error al cargar transacciones: ${e.message}")
            }
    }

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

        val transactionMap = hashMapOf(
            "type" to transaction.type.name,
            "amount" to transaction.amount,
            "description" to transaction.description,
            "date" to transaction.date,
            "paymentMethod" to transaction.paymentMethod,
            "timestamp" to transaction.timestamp
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .document(transaction.id)
            .set(transactionMap)
            .addOnSuccessListener {
                // Agregar a la lista local inmediatamente
                val updatedTransactions = listOf(transaction) + transactions
                transactions = updatedTransactions.sortedByDescending { it.timestamp }
                errorMessage = null
                onSuccess()
                println("Transacción agregada correctamente")
            }
            .addOnFailureListener { exception ->
                val error = "Error al agregar la transacción: ${exception.message}"
                errorMessage = error
                onFailure(error)
                println("Error al agregar transacción: ${exception.message}")
            }
    }

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
                // Eliminar de la lista local
                transactions = transactions.filter { it.id != transactionId }
                errorMessage = null
                onSuccess()
                println("Transacción eliminada correctamente")
            }
            .addOnFailureListener { exception ->
                val error = "Error al eliminar la transacción: ${exception.message}"
                errorMessage = error
                onFailure(error)
                println("Error al eliminar transacción: ${exception.message}")
            }
    }

    fun updateTransaction(transaction: Transaction) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        val transactionMap = hashMapOf(
            "type" to transaction.type.name,
            "amount" to transaction.amount,
            "description" to transaction.description,
            "date" to transaction.date,
            "paymentMethod" to transaction.paymentMethod,
            "timestamp" to transaction.timestamp
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .document(transaction.id)
            .set(transactionMap)
            .addOnSuccessListener {
                // Actualizar en la lista local
                transactions = transactions.map {
                    if (it.id == transaction.id) transaction else it
                }.sortedByDescending { it.timestamp }
                isLoading = false
                println("Transacción actualizada correctamente")
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al actualizar la transacción: ${e.message}"
                isLoading = false
                println("Error al actualizar transacción: ${e.message}")
            }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen = screen
    }

    fun updateCurrentScreen(screen: Screen) {
        _currentScreen = screen
    }

    fun updateSavingGoal(goal: SavingGoal) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        val userRef = db.collection("users").document(currentUser.uid)
        val goalRef = userRef.collection("savingGoals").document(goal.id)

        goalRef.set(
            mapOf(
                "name" to goal.name,
                "targetAmount" to goal.targetAmount,
                "currentAmount" to goal.currentAmount
            )
        )
            .addOnSuccessListener {
                loadSavingGoals()
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al actualizar la meta: ${e.message}"
                isLoading = false
            }
    }

    fun deleteSavingGoal(goalId: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        isLoading = true
        errorMessage = null

        val userRef = db.collection("users").document(currentUser.uid)
        val goalRef = userRef.collection("savingGoals").document(goalId)

        goalRef.delete()
            .addOnSuccessListener {
                loadSavingGoals()
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al eliminar la meta: ${e.message}"
                isLoading = false
            }
    }

    fun clearError() {
        errorMessage = null
    }

    fun showAddGoalModal() {
        currentEditingGoal = null
        showAddGoalModal = true
    }

    fun hideAddGoalModal() {
        currentEditingGoal = null
        showAddGoalModal = false
    }

    fun showEditGoalModal(goal: SavingGoal) {
        currentEditingGoal = goal
        showAddGoalModal = true
    }

    fun saveGoal(name: String, targetAmount: Double) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado"
            return
        }

        val userRef = db.collection("users").document(currentUser.uid)
        val newGoalRef = userRef.collection("savingGoals").document()

        val goal = SavingGoal(
            id = newGoalRef.id,
            name = name,
            targetAmount = targetAmount,
            currentAmount = 0.0
        )

        newGoalRef.set(goal)
            .addOnSuccessListener {
                loadSavingGoals()
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al guardar la meta: ${e.message}"
            }
    }

    fun updateGoal(goal: SavingGoal) {
        val goalRef = db.collection("users").document(auth.currentUser?.uid ?: "")
            .collection("savingGoals").document(goal.id)

        val goalData = mapOf(
            "name" to goal.name,
            "targetAmount" to goal.targetAmount,
            "currentAmount" to goal.currentAmount
        )

        goalRef.set(goalData)
            .addOnSuccessListener {
                loadSavingGoals()
            }
            .addOnFailureListener { e ->
                errorMessage = "Error al actualizar la meta: ${e.message}"
            }
    }

    // Función para verificar si hay usuario autenticado
    fun checkAuthenticationAndLoadData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadSavingGoals()
            loadTransactions()
        } else {
            errorMessage = "No hay usuario autenticado"
        }
    }

    fun logout(navController: NavController) {
        authViewModel.logout(navController)
    }
}