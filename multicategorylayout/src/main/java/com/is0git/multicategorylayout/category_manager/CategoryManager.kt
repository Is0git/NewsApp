package com.is0git.multicategorylayout.category_manager

import com.is0git.multicategorylayout.category_controller.CategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_modifier.CategoryModifier
import com.is0git.multicategorylayout.listeners.CategoryListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryManager<T>(
    private val categoryModifier: CategoryModifier<T>,
    private val categoryController: CategoryController<T>,
    private val categoryListener: CategoryListener<T>
) : CategoryListener<T> by categoryListener {
    val categories: MutableMap<String, Category<T>> = mutableMapOf()

    suspend fun addCategories(categories: List<Category<T>>) {
        for (c in categories) {
            addCategory(c)
        }
    }

    private suspend fun addCategory(category: Category<T>) {
        coroutineScope {
            launch(Dispatchers.Default) { categoryModifier.modifyCategory(category) }
            categories.put(category.id, category)
        }
        withContext(Dispatchers.Main) {
            onCategoryAdded(category)
        }
    }

    suspend fun resetCategories() {
        categoryController.resetCategories(categories)
    }

    suspend fun updateCategories() {
        categoryController.updateCategories(categories)
    }
}
