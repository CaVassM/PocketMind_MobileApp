package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val isDefault: Boolean = false,
    val totalAmount: Double = 0.0
)

data class CategoriasUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val overallBalance: Double = 0.0
)

class CategoriasViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Estado para la pantalla actual del BottomNavigationBar
    var currentScreen by mutableStateOf(Screen.Categorias)
        private set

    private val _uiState = MutableStateFlow(CategoriasUiState())
    val uiState: StateFlow<CategoriasUiState> = _uiState.asStateFlow()

    private var categoriesListener: ListenerRegistration? = null
    private var transactionsListener: ListenerRegistration? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val userId = auth.currentUser?.uid ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        // Escuchar cambios en las categorías del usuario
        categoriesListener = firestore
            .collection("users")
            .document(userId)
            .collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { doc ->
                        try {
                            Category(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                icon = doc.getString("icon") ?: "",
                                color = doc.getString("color") ?: "#000000",
                                isDefault = doc.getBoolean("isDefault") ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        isLoading = false
                    )

                    // Cargar los montos de las transacciones
                    loadCategoryAmounts()
                }
            }
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    private fun loadCategoryAmounts() {
        val userId = auth.currentUser?.uid ?: return

        // Escuchar cambios en las transacciones para calcular montos por categoría
        transactionsListener = firestore
            .collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Agrupar transacciones por categoría
                    val categoryAmounts = mutableMapOf<String, Double>()
                    var totalIncome = 0.0
                    var totalExpenses = 0.0

                    snapshot.documents.forEach { doc ->
                        try {
                            val amount = doc.getDouble("amount") ?: 0.0
                            val type = doc.getString("type") ?: ""
                            val categoryIdRef = doc.get("categoryId") as? com.google.firebase.firestore.DocumentReference

                            if (categoryIdRef != null) {
                                val categoryId = categoryIdRef.id
                                val currentAmount = categoryAmounts[categoryId] ?: 0.0

                                when (type) {
                                    "INCOME" -> {
                                        categoryAmounts[categoryId] = currentAmount + amount
                                        totalIncome += amount
                                    }
                                    "EXPENSE" -> {
                                        categoryAmounts[categoryId] = currentAmount + amount
                                        totalExpenses += amount
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Ignorar transacciones con errores
                        }
                    }

                    // Actualizar categorías con sus montos
                    val updatedCategories = _uiState.value.categories.map { category ->
                        category.copy(totalAmount = categoryAmounts[category.id] ?: 0.0)
                    }

                    _uiState.value = _uiState.value.copy(
                        categories = updatedCategories,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        overallBalance = totalIncome - totalExpenses
                    )
                }
            }
    }

    fun initializeUserCategories() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                // Verificar si el usuario ya tiene categorías
                val userCategories = firestore
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .get()
                    .await()

                if (userCategories.isEmpty) {
                    // Copiar categorías globales al usuario
                    val globalCategories = firestore
                        .collection("categories")
                        .get()
                        .await()

                    val batch = firestore.batch()

                    globalCategories.documents.forEach { doc ->
                        val categoryRef = firestore
                            .collection("users")
                            .document(userId)
                            .collection("categories")
                            .document(doc.id)

                        batch.set(categoryRef, doc.data ?: mapOf<String, Any>())
                    }

                    batch.commit().await()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al inicializar categorías: ${e.message}"
                )
            }
        }
    }

    fun addCustomCategory(name: String, icon: String, color: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                val categoryData = mapOf(
                    "name" to name,
                    "icon" to icon,
                    "color" to color,
                    "isDefault" to false
                )

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .add(categoryData)
                    .await()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar categoría: ${e.message}"
                )
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .document(categoryId)
                    .delete()
                    .await()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar categoría: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        categoriesListener?.remove()
        transactionsListener?.remove()
    }
}