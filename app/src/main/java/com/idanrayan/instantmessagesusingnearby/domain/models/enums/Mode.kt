package com.idanrayan.instantmessagesusingnearby.domain.models.enums

import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.utils.ResourceUtils.getString

/**
 * The request status
 *
 */
enum class Mode {

    CONNECTED,
    OFF,
    LOADING;

    fun modeName(): String = when (this) {
        CONNECTED -> getString(R.string.on_)
        LOADING -> getString(R.string.loading)
        OFF -> getString(R.string.off_)
    }
}