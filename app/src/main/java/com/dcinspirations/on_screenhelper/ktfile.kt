package com.dcinspirations.on_screenhelper

import java.util.Arrays

class ktfile {
    internal var popularextensions = arrayOf("png", "jpg", "gif")

    fun checkExtension(filename: String): Boolean {
        val extension: String = filename.subSequence(filename.lastIndexOf(".") + 1,filename.length).toString().toLowerCase()
        return Arrays.asList(*popularextensions).contains(extension)
    }
}
