package com.enesbaydar.todolistapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.enesbaydar.todolistapp.ui.data.TodoEntity
import com.enesbaydar.todolistapp.ui.data.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TodoScreen(viewModel: TodoViewModel){
    val showEditDialog = remember{mutableStateOf(false)}// 1. Defter: "Pencere açık mı?"
    val taskToEdit = remember{mutableStateOf<TodoEntity?>(null)}// 2. Defter: "Şu an HANGİ görevi düzenliyoruz?"
    val showDeleteDialog = remember{mutableStateOf(false)}
    val taskToDelete = remember{mutableStateOf<TodoEntity?>(null)}
    val showDialog = remember{mutableStateOf(false)}
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            TopAppBar(
                title = { Text("My Tasks", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start =24.dp))},
                navigationIcon = {Icon(Icons.Default.Menu, contentDescription = "Menu")}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {showDialog.value = true}) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(uiState.todos.size){ index ->
                    val currenttodo = uiState.todos[index]
                    TaskItem(
                        title = currenttodo.title,
                        description = currenttodo.description,
                        onEditClick = {
                            taskToEdit.value = currenttodo // "Tamam, düzenleyeceğimiz görev bu, aklıma yazdım."
                            showEditDialog.value = true // "Hemen ekrana düzenleme penceresini çizin!"
                        },

                        onDeleteClick = {
                            taskToDelete.value = currenttodo
                            showDeleteDialog.value = true

                        }
                        , isCompleted = currenttodo.isCompleted,
                        onCheckedChange = {
                            val updatedTask=currenttodo.copy(isCompleted = it)
                            viewModel.updateTodo(updatedTask,updatedTask.title,updatedTask.description)
                        }

                    )
                }
            }
        }
    )

    if(showDialog.value){
        AddTodoDialog(
            onDismissRequest = {showDialog.value = false},
            onConfirmClick = { title, description ->
                viewModel.addTodo(title, description)
                showDialog.value = false
            }
        )
    }
    if(showEditDialog.value && taskToEdit.value != null){
        EditTodoDialog(
            todoToEdit = taskToEdit.value!!, // Düzenlenecek görevi veriyoruz
            onDismissRequest = { showEditDialog.value = false },
            onConfirmClick = { newTitle, newDescription ->
                // 1. O anki görevin birebir kopyasını çıkar, sadece title ve description'ı yeni yazılanlarla değiştir.
                val updatedTask = taskToEdit.value!!.copy(
                    title = newTitle,
                    description = newDescription
                )

                // 2. ViewModel'a bu güncellenmiş görevi veritabanına kaydetmesini söyle
                viewModel.updateTodo(updatedTask, updatedTask.title, updatedTask.description ) // Not: ViewModel'ındaki updateTodo bu parametreleri bekliyor.

                // 3. İşlem bitince pencereyi kapat
                showEditDialog.value = false
            }
        )
    }
    if(showDeleteDialog.value && taskToDelete.value != null){
        AlertDialog(
            onDismissRequest = {showDeleteDialog.value = false},
            title = { Text("Görevi Sil") },
            text = { Text("Bu görevi silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTodo(taskToDelete.value!!)
                        showDeleteDialog.value = false // Tıklanınca hem sil hem de pencereyi kapat
                    }
                ) {
                    Text("Sil") // Butonun üzerinde yazacak olan metin
                }
            },
            dismissButton = {
                TextButton(onClick = {showDeleteDialog.value = false}) {
                    Text("İptal")
                }

            }

        )

    }
}

@Composable
fun TaskItem(title: String,
             description: String,
             isCompleted: Boolean,
             onEditClick: () -> Unit,
             onDeleteClick: () -> Unit,
             onCheckedChange:(Boolean)->Unit){
    Card( modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp))
    {
        Row() {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = {
                           current->
                    onCheckedChange(current)
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title,
                    fontWeight = FontWeight.Bold,
                    textDecoration =
                        if(isCompleted){
                            TextDecoration.LineThrough
                        }
                        else{
                            TextDecoration.None
                        }
                )
                Text(text = description,
                    textDecoration =
                    if(isCompleted){
                        TextDecoration.LineThrough
                    }
                    else{
                        TextDecoration.None
                    })

            }
            IconButton(onClick = {
                onEditClick()
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = {
                onDeleteClick()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
@Composable
fun AddTodoDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmClick:(String,String) -> Unit
){
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Yeni Görev Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Başlık") }
                )
                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text("Açıklama") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Boş değilse ViewModel'a gönder
                    if (title.value.isNotBlank()) {
                        onConfirmClick(title.value, description.value)
                    }
                }
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("İptal")
            }
        }

 )


}
@Composable
fun EditTodoDialog(
    todoToEdit: TodoEntity, // YENİ: Düzenlenecek olan görevi parametre olarak alıyoruz
    onDismissRequest: () -> Unit = {},
    onConfirmClick: (String, String) -> Unit // title ve description dönecek
) {
    // KRİTİK NOKTA: mutableStateOf içine boş String ("") YERİNE,
    // var olan görevin başlığını ve açıklamasını koyuyoruz.
    // Böylece pencere açıldığında eski yazılar dolu gelir!
    val title = remember { mutableStateOf(todoToEdit.title) }
    val description = remember { mutableStateOf(todoToEdit.description) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Görevi Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Başlık") }
                )
                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text("Açıklama") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.value.isNotBlank()) {
                        onConfirmClick(title.value, description.value)
                    }
                }
            ) {
                Text("Kaydet") // "Ekle" yerine "Kaydet" yaptık
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("İptal")
            }
        }
    )
}

