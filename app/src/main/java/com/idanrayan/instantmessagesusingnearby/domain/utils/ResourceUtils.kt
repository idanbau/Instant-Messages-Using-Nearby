package com.idanrayan.instantmessagesusingnearby.domain.utils

import androidx.annotation.StringRes
import com.idanrayan.instantmessagesusingnearby.MainApplication

object ResourceUtils {

    /**
     * Get string from the resources without context
     *
     * @param id
     * @param params
     */
    fun getString(@StringRes id: Int, vararg params: Any): String =
        MainApplication.res.getString(id, *params)
}