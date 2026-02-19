package com.example.done

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.done.ui.theme.DoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoneTheme{
                TodoApp()
            }

        }
    }
}

@Composable
fun TodoApp() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    val todos = remember { mutableStateListOf<TodoItem>() }

    // 启动时加载数据
    LaunchedEffect(Unit) {
        todos.addAll(TodoStore.loadTodos(context))
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // ===== 输入区域 =====
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("新任务",style = MaterialTheme.typography.labelLarge) },
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (text.isNotBlank()) {
                    todos.add(TodoItem(text))
                    text = ""

                    scope.launch {
                        TodoStore.saveTodos(context, todos)
                    }
                }
            }) {
                Text("添加")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 待办任务 =====
        Text("待办任务", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(todos.filter { !it.done }) { todo ->
                TodoRow(todo) {
                    scope.launch {
                        TodoStore.saveTodos(context, todos)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 已完成任务 =====
        Text("已完成", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(todos.filter { it.done }) { todo ->
                TodoRow(todo) {
                    scope.launch {
                        TodoStore.saveTodos(context, todos)
                    }
                }
            }
        }
    }
}

@Composable
fun TodoRow(todo: TodoItem, onChanged: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.done,
                onCheckedChange = {
                    todo.done = it
                    onChanged()
                }
            )

            Text(todo.text,
                style = MaterialTheme.typography.bodyLarge  )
        }
    }
}

// 使用 mutableStateOf 才能触发 UI 更新
class TodoItem(text: String) {
    val text by mutableStateOf(text)
    var done by mutableStateOf(false)
}
