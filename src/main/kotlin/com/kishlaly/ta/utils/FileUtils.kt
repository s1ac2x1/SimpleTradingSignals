package com.kishlaly.ta.utils

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class FileUtils {

    companion object {

        fun appendToFile(filename: String, content: String) {
            val file = File(filename)
            try {
                FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8, true)
                FileUtils.writeStringToFile(file, System.lineSeparator(), StandardCharsets.UTF_8, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun writeToFile(name: String, content: String) {
            try {
                Files.write(Paths.get(name), content.toByteArray())
            } catch (e: IOException) {
                println(e.message)
            }
        }

    }

}