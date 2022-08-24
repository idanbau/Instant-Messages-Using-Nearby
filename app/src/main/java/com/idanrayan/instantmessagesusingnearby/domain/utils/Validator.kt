package com.idanrayan.instantmessagesusingnearby.domain.utils

import com.idanrayan.instantmessagesusingnearby.MainApplication
import com.idanrayan.instantmessagesusingnearby.R


/**
 * Strings validation
 */
class Validator(
    private val value: String,
    private val name: String,
) {

    private val errors = mutableListOf<String>()

    /**
     * The value can't be empty or blank
     */
    fun required() = apply {
        if (value.isBlank()) {
            errors.add(
                MainApplication.res.getString(R.string.cannot_be_empty, name)
            )
        }
    }

    /**
     * The value length can't be less then [min]
     *
     * @param min
     */
    fun max(min: Int) = apply {
        if (value.length > min) {
            errors.add(
                MainApplication.res.getString(R.string.cannot_be_greater, name, min.toString())
            )
        }
    }

    /**
     * @return if [errors] is empty return null else return the first value. null mean [value] is valid
     */
    fun build(): String? = errors.firstOrNull()
}

fun String.validate(name: String): Validator =
    Validator(this, name)