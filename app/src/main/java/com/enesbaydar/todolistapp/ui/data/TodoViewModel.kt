package com.enesbaydar.todolistapp.ui.data // Bu dosyanın bulunduğu paket adı. Uygulamanın veri katmanının bir parçası olduğunu belirtir.

import androidx.lifecycle.ViewModel // Android Jetpack'in ViewModel sınıfını içeri aktarır. UI ile ilgili verileri yaşam döngüsüne duyarlı bir şekilde saklamak için kullanılır.
import androidx.lifecycle.viewModelScope // ViewModel'e bağlı bir CoroutineScope sağlar. ViewModel yok edildiğinde bu scope'taki coroutine'ler otomatik olarak iptal edilir.
import com.enesbaydar.todolistapp.ui.data.repository.TodoRepository // 'Yapılacaklar' verilerine erişmek için oluşturduğumuz Repository sınıfını içeri aktarır.
import dagger.hilt.android.lifecycle.HiltViewModel // Dagger Hilt kütüphanesinin bu ViewModel'e bağımlılıkları enjekte etmesini sağlayan bir ek açıklama (annotation).
import kotlinx.coroutines.flow.MutableStateFlow // Değeri güncellenebilen bir state flow. Genellikle ViewModel içinde özel (private) olarak kullanılır.
import kotlinx.coroutines.flow.StateFlow // Sadece okunabilir bir state flow. UI tarafından veri akışını güvenli bir şekilde gözlemlemek için kullanılır.
import kotlinx.coroutines.flow.asStateFlow // MutableStateFlow'u, dışarıya sadece okunabilir bir StateFlow olarak sunmak için kullanılır.
import kotlinx.coroutines.flow.update // StateFlow'un mevcut değerini güvenli (atomik) bir şekilde güncellemek için kullanılan bir yardımcı fonksiyon.
import kotlinx.coroutines.launch // Askıya alınabilir bir fonksiyonu (suspend function) ana iş parçacığını engellemeden yeni bir coroutine içinde başlatır.
import javax.inject.Inject // Dagger Hilt tarafından bağımlılıkların nereye enjekte edileceğini belirtmek için kullanılan bir ek açıklama.

// Kullanıcı arayüzünün (UI) anlık durumunu temsil eden bir veri sınıfı (data class).
data class TodoUiState(
    val todos: List<TodoEntity> = emptyList(), // Ekranda gösterilecek 'yapılacaklar' listesi. Başlangıçta boştur.
    val isLoading: Boolean = false, // Verilerin yüklenip yüklenmediğini belirten durum. Başlangıçta 'false' (yüklenmiyor).
    val error: String? = null // Bir hata oluşursa hata mesajını tutar. Başlangıçta 'null' (hata yok).
)


@HiltViewModel // Bu sınıfın bir Hilt ViewModel'i olduğunu belirtir. Hilt, bu sınıfa bağımlılıkları otomatik olarak enjekte edebilir.
class TodoViewModel @Inject constructor( // ViewModel'in ana yapıcısı (constructor). @Inject ile Hilt'in bağımlılıkları (örn: TodoRepository) buraya enjekte etmesi sağlanır.
    private val todoRepository: TodoRepository // 'Yapılacaklar' verilerine erişimi sağlayan repository (veri kaynağı). private val olarak tanımlanarak sadece bu sınıf içinde erişilebilir yapılır.
) : ViewModel() { // Bu sınıfın bir ViewModel olduğunu ve Android'in yaşam döngüsü (lifecycle) yönetiminden faydalanacağını belirtir.
    // 1. Kendi kullanacağın (özel) - ViewModel içinde değiştirilebilen, özel (private) UI durumu.
    private val _uiState = MutableStateFlow(TodoUiState())
    // 2. UI'a verdiğin (genel) - UI (Composable'lar) tarafından gözlemlenecek, sadece okunabilir UI durumu.
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()


    // ViewModel ilk oluşturulduğunda çalışan özel bir bloktur.
    init {
        getAllTodos() // Bu blok içinde tüm 'yapılacaklar' listesini getiren fonksiyon çağırılır.
    }

    // Tüm 'yapılacaklar' listesini veritabanından getiren fonksiyon.
    fun getAllTodos() {
        viewModelScope.launch { // ViewModel'in yaşam döngüsüne bağlı bir coroutine başlatır.
            _uiState.update { it.copy(isLoading = true) } // Veri yüklemesi başlarken UI durumunu 'yükleniyor' olarak günceller.
            // Bana verileri ver ve değişiklik olursa haber ver - Repository'den gelen 'yapılacaklar' listesi akışını (Flow) dinlemeye başlar.
            todoRepository.getAllTodos().collect { list ->
                _uiState.value = _uiState.value.copy( // Akıştan yeni bir liste geldiğinde UI durumunu günceller.
                    todos = list, // 'yapılacaklar' listesini yeni gelen liste ile günceller.
                    isLoading = false // Veri yüklemesi bittiği için 'yükleniyor' durumunu 'false' yapar.
                )
            }
        }
    }

    // Yeni bir 'yapılacak' öğesi ekleyen fonksiyon.
    fun addTodo(title: String,description: String){
        if(title.isBlank())return // Eğer başlık boş veya sadece boşluk karakterlerinden oluşuyorsa, işlemi iptal et ve fonksiyondan çık.
        viewModelScope.launch { // Arka planda çalışacak yeni bir coroutine başlatır.
            // Repository aracılığıyla yeni bir TodoEntity'yi (yapılacak öğesi) veritabanına ekler.
            todoRepository.insertTodo(TodoEntity(title = title, description = description, isCompleted = false))
        }
    }
    
    // Var olan bir 'yapılacak' öğesini silen fonksiyon.
    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch { // Arka planda çalışacak yeni bir coroutine başlatır.
            todoRepository.deleteTodo(todo) // Repository aracılığıyla ilgili 'yapılacak' öğesini veritabanından siler.
        }
    }

    // Var olan bir 'yapılacak' öğesini güncelleyen fonksiyon.
    fun updateTodo(todo: TodoEntity,newTitle: String,newDescription: String) {
        viewModelScope.launch { // Arka planda çalışacak yeni bir coroutine başlatır.
            val updatedTodo = todo.copy(title = newTitle, description = newDescription) // Mevcut yapılacak öğesinin bir kopyasını yeni başlık ve açıklama ile oluşturur.
            todoRepository.updateTodo(updatedTodo) // Repository aracılığıyla güncellenmiş 'yapılacak' öğesini veritabanına kaydeder.
        }
    }
}
