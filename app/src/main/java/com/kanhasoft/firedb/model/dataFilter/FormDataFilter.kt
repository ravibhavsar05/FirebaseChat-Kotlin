package com.kanhasoft.firedb.model.dataFilter

import com.google.common.base.Predicate
import com.kanhasoft.firedb.model.UserData

import java.util.regex.Pattern

class FormDataFilter(regex: String) : Predicate<UserData> {
    private val pattern: Pattern

    init {
        pattern = Pattern.compile(regex)
    }

    override fun apply(employee: UserData?): Boolean {
        return pattern.matcher(employee!!.display_name!!.toString().toLowerCase()).find() ||
                pattern.matcher(employee.email!!.toString().toLowerCase()).find() ||
                pattern.matcher(employee.phone!!.toString()).find()
    }
}