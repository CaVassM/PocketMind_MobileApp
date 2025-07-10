package com.example.ta_movil.ViewModels

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
            .orderBy("date", Query.Direction.DESCENDING)
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
    
    fun addTransaction(transaction: Transaction) {
        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .addOnSuccessListener {
                loadTransactions()
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al agregar la transacción: ${exception.message}"
            }
    }
    
    fun deleteTransaction(transactionId: String) {
        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                loadTransactions()
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al eliminar la transacción: ${exception.message}"
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
}

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
