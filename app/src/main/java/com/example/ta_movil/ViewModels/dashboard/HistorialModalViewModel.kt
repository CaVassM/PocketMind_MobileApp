package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_movil.Model.dashboard.transactionState
import com.example.ta_movil.ViewModels.dashboard.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HistorialModalViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Estado de la transacción
    private var _transactionState by mutableStateOf(transactionState())
    val transactionState: transactionState get() = _transactionState

    // Errores de validación
    private var _amountError by mutableStateOf("")
    val amountError: String get() = _amountError

    private var _descriptionError by mutableStateOf("")
    val descriptionError: String get() = _descriptionError

    private var _categoryError by mutableStateOf("")
    val categoryError: String get() = _categoryError

    private var _isSubmitting by mutableStateOf(false)
    val isSubmitting: Boolean get() = _isSubmitting

    // Categorías
    private var _categories by mutableStateOf<List<Category>>(emptyList())
    val categories: List<Category> get() = _categories

    private var _selectedCategoryId by mutableStateOf("")
    val selectedCategoryId: String get() = _selectedCategoryId

    private var _isLoadingCategories by mutableStateOf(false)
    val isLoadingCategories: Boolean get() = _isLoadingCategories

    init {
        loadCategories()
    }

    /** Carga las categorías del usuario **/
    private fun loadCategories() {
        val userId = auth.currentUser?.uid ?: return
        _isLoadingCategories = true

        viewModelScope.launch {
            try {
                val snapshot = firestore
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .get()
                    .await()

                val list = snapshot.documents.mapNotNull { doc ->
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

                _categories = list
                _isLoadingCategories = false

                if (list.isNotEmpty() && _selectedCategoryId.isEmpty()) {
                    _selectedCategoryId = list.first().id
                }
            } catch (e: Exception) {
                _isLoadingCategories = false
                println("Error al cargar categorías: ${e.message}")
            }
        }
    }

    /** Updates **/
    fun updateTransactionType(type: TransactionType) {
        _transactionState = _transactionState.copy(type = type)
    }

    fun updateAmount(amount: String) {
        _transactionState = _transactionState.copy(amount = amount)
        _amountError = ""
    }

    fun updateDescription(description: String) {
        _transactionState = _transactionState.copy(description = description)
        _descriptionError = ""
    }

    fun updatePaymentMethod(method: String) {
        _transactionState = _transactionState.copy(paymentMethod = method)
    }

    fun updateSelectedDate(date: Date) {
        _transactionState = _transactionState.copy(selectedDate = date)
    }

    fun updateSelectedCategory(categoryId: String) {
        _selectedCategoryId = categoryId
        _categoryError = ""
    }

    /** Validación del formulario **/
    fun validateForm(): Boolean {
        var isValid = true

        // Monto
        if (_transactionState.amount.isBlank()) {
            _amountError = "El monto es requerido"
            isValid = false
        } else {
            try {
                val v = _transactionState.amount.toDouble()
                if (v <= 0) {
                    _amountError = "El monto debe ser mayor a 0"
                    isValid = false
                } else {
                    _amountError = ""
                }
            } catch (e: NumberFormatException) {
                _amountError = "Ingrese un monto válido"
                isValid = false
            }
        }

        // Descripción
        if (_transactionState.description.isBlank()) {
            _descriptionError = "La descripción es requerida"
            isValid = false
        } else if (_transactionState.description.length < 3) {
            _descriptionError = "La descripción debe tener al menos 3 caracteres"
            isValid = false
        } else {
            _descriptionError = ""
        }

        // Categoría
        if (_selectedCategoryId.isEmpty()) {
            _categoryError = "Debe seleccionar una categoría"
            isValid = false
        } else {
            _categoryError = ""
        }

        return isValid
    }

    /** Formatea un Date a dd/MM/yyyy **/
    fun formatDate(date: Date): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return fmt.format(date)
    }

    /** Guarda la transacción en Firestore **/
    fun saveTransaction(
        dashboardViewModel: DashboardViewModel,
        onSuccess: () -> Unit
    ) {
        if (!validateForm()) return

        _isSubmitting = true

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val categoryRef = firestore
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .document(_selectedCategoryId)

                // Preparamos los valores
                val dateString = formatDate(_transactionState.selectedDate)
                val timestampLong = _transactionState.selectedDate.time

                val data = mapOf(
                    "type" to _transactionState.type.name,
                    "amount" to _transactionState.amount.toDouble(),
                    "description" to _transactionState.description.trim(),
                    "date" to dateString,            // String "15/07/2025"
                    "paymentMethod" to _transactionState.paymentMethod,
                    "categoryId" to categoryRef,
                    "timestamp" to timestampLong     // 1752…
                )

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("transactions")
                    .add(data)
                    .await()

                resetForm()
                _isSubmitting = false
                onSuccess()

            } catch (e: Exception) {
                _isSubmitting = false
                println("Error al guardar transacción: ${e.message}")
            }
        }
    }

    /** Resetea el formulario **/
    fun resetForm() {
        _transactionState = transactionState(
            type = TransactionType.EXPENSE,
            amount = "",
            description = "",
            paymentMethod = "Efectivo",
            selectedDate = Date()
        )
        _amountError = ""
        _descriptionError = ""
        _categoryError = ""
        _isSubmitting = false
        if (_categories.isNotEmpty()) {
            _selectedCategoryId = _categories.first().id
        }
    }

    /** Métodos de pago disponibles **/
    fun getPaymentMethods(): List<String> =
        listOf("Efectivo", "Tarjeta", "Transferencia", "Otro")
}
