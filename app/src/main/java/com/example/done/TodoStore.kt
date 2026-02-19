package com.example.done

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore("todo_prefs")

object TodoStore {

    private val TODOS_KEY = stringPreferencesKey("todos")
    suspend fun saveTodos(context: Context, list: List<TodoItem>) {
        val text = list.joinToString("|") {
            "${it.text},${it.done}"
        }
        context.dataStore.edit {
            it[TODOS_KEY] = text
        }
    }

    suspend fun loadTodos(context: Context): List<TodoItem> {
        val prefs = context.dataStore.data.first()
        val text = prefs[TODOS_KEY] ?: return emptyList()

        return text.split("|").map {
            val parts = it.split(",")
            TodoItem(parts[0]).apply {
                done = parts[1].toBoolean()
            }
        }
    }
}
