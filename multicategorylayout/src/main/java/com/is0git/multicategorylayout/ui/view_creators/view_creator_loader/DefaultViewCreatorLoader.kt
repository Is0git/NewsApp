package com.is0git.multicategorylayout.ui.view_creators.view_creator_loader

import com.is0git.multicategorylayout.ui.view_creators.ViewCreator

class DefaultViewCreatorLoader : ViewCreatorLoader() {
    override fun loadViewCreators(vararg items: ViewCreator?) {
        viewCreators = items
    }
}