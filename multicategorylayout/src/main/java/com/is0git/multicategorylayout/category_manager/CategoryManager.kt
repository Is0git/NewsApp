package com.is0git.multicategorylayout.category_manager

import com.is0git.multicategorylayout.category_controller.CategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_manager.adapter.AdapterController
import com.is0git.multicategorylayout.category_modifier.CategoryModifier
import com.is0git.multicategorylayout.listeners.CategoryListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CategoryManager(
    private val categoryModifier: CategoryModifier,
    private val categoryController: CategoryController,
    private val categoryListener: CategoryListener
) : CategoryListener by categoryListener,
    AdapterController {
    val categories: MutableMap<String, Category<*>> = mutableMapOf()

    suspend fun addCategories(categories: List<Category<*>>) {
        categories.forEachIndexed { pos, category ->
            addCategory(category, pos)
        }
    }

    suspend fun addCategory(category: Category<*>, position: Int) {
        coroutineScope {
            launch(Dispatchers.Default) { categoryModifier.modifyCategory(category) }
            categories.put(category.id, category)
        }
        withContext(Dispatchers.Main) {
            categoryAdded(category, position)
        }
    }

    fun removeCategory(position: Int) {
        val category = categories.values.elementAt(position)
        categories.remove(category.id)
        categoryRemoved(category, position)
    }

    fun updateCategory(position: Int) {
        val category = categories.values.elementAt(position)
        if (categories.containsKey(category.id)) throw IllegalStateException("category not found")
        categoryChanged(category, position)
    }

    //not implemented yet
    suspend fun resetCategories() {
        categoryController.resetCategories(categories)
    }

    //not implemented yet
    suspend fun updateCategories() {
        categoryController.updateCategories(categories)
    }

    @Suppress("unchecked_cast")
    override fun updateCategoryAdapter(categoryId: String, list: List<*>) {
        val containsCategory = categories.containsKey(categoryId)
        if (!containsCategory) return
        else {
            val category = categories[categoryId]!!
            category.adapter.submitList(list as List<Nothing>)
        }
    }

    fun getCount(): Int {
        return categories.size
    }
}
