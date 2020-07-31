package com.is0git.multicategorylayout.ui.category_layout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.is0git.multicategorylayout.category_controller.DefaultCategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_manager.CategoryManager
import com.is0git.multicategorylayout.category_modifier.DefaultCategoryModifier
import com.is0git.multicategorylayout.listeners.CategoryListener
import com.is0git.multicategorylayout.ui.CategoryUIManager
import com.is0git.multicategorylayout.ui.UIManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryLayout : ConstraintLayout {

    private lateinit var categoryManager: CategoryManager<*>
    lateinit var uiCreator: UIManager<Category<*>>
    private var isInitialized = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun <T> init() {
        val categoryModifier = DefaultCategoryModifier<T>()
        val categoryController = DefaultCategoryController<T>()
        val categoryListener: CategoryListener<T> = object : CategoryListener<T> {
            override fun onCategoryChange(category: Category<T>) {

            }

            override fun onCategoryRemove(categoryId: String) {

            }

            override fun onCategoryAdded(category: Category<T>) {
                createCategoryGroup(category)
            }

        }
        uiCreator = CategoryUIManager<T>(this)
        categoryManager = CategoryManager(categoryModifier, categoryController, categoryListener)
        isInitialized = true
    }

    @Suppress("UNCHECKED_CAST")
     fun <T> addCategories(categories: List<Category<T>>) {
        init<T>()
        CoroutineScope(Dispatchers.Default).launch {
            (categoryManager as CategoryManager<T>).addCategories(categories)
        }
    }

    fun getCategoriesCount(): Int {
        return categoryManager.categories.count()
    }

    fun getCategoryGroupSize(): Int {
       return uiCreator.categoryGroupSize
    }

    fun<T> updateAdapter(categoryId: String?, items: List<*>) {
        categoryId ?: return
        categoryManager.categories[categoryId]?.adapter?.submitList(items as List<Nothing>)
    }

    private fun createCategoryGroup(category: Category<*>) {
        uiCreator.createUI(category)
    }
}