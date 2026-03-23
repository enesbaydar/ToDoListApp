package com.enesbaydar.todolistapp.ui.data.repository // Bu dosyanın bulunduğu paket adı. Veri erişim katmanının bir parçası olduğunu belirtir.

import com.enesbaydar.todolistapp.ui.data.TodoDao // Veritabanı işlemlerini (CRUD: Create, Read, Update, Delete) tanımlayan arayüz (DAO).
import com.enesbaydar.todolistapp.ui.data.TodoEntity // Veritabanı tablosundaki bir satırı temsil eden veri sınıfı (Entity).
import kotlinx.coroutines.flow.Flow // Zaman içinde bir dizi değer üretebilen asenkron bir veri akışıdır. 'Yapılacaklar' listesini anlık olarak dinlemek için kullanılır.
import javax.inject.Inject // Dagger Hilt'in bağımlılıkları enjekte etmesini sağlayan standart bir Java ek açıklaması.

// Repository sınıfı, veri kaynaklarını (veritabanı, ağ vb.) soyutlar ve ViewModel'e temiz bir API sunar.
// @Inject constructor() ile Hilt'in bu sınıfı nasıl oluşturacağını ve bağımlılıklarını (TodoDao) nasıl sağlayacağını bilmesini sağlarız.
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao // Veritabanı erişim nesnesi (DAO). 'private val' ile sadece bu sınıf içinde erişilebilir yapılır.
) {

    // Tüm 'yapılacaklar' listesini bir Flow (akış) olarak döndürür.
    // Bu sayede veritabanındaki herhangi bir değişiklik (ekleme, silme, güncelleme) otomatik olarak bu akışı dinleyenlere iletilir.
    fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    // Veritabanına yeni bir 'yapılacak' öğesi eklemek için askıya alınabilir (suspend) bir fonksiyon.
    // 'suspend' anahtar kelimesi, bu fonksiyonun bir coroutine içinde çağrılması gerektiğini ve ana iş parçacığını engellemeyeceğini belirtir.
    suspend fun insertTodo(todo: TodoEntity) {
        todoDao.insertTodo(todo) // DAO üzerinden veritabanına ekleme işlemini gerçekleştirir.
    }

    // Veritabanından mevcut bir 'yapılacak' öğesini silmek için askıya alınabilir bir fonksiyon.
    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo.id) // DAO üzerinden silme işlemini gerçekleştirir.
    }

    // Veritabanındaki bir 'yapılacak' öğesini güncellemek için askıya alınabilir bir fonksiyon.
    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo) // DAO üzerinden güncelleme işlemini gerçekleştirir.
    }
}
