package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

//fun main() {
//    val tasks = (1..3).map {
//        var prompt = Combiner.combine(
//            listOf(
//                "openai/katze101/breeds",
//                "openai/katze101/age",
//                "openai/katze101/care",
//            )
//        )
//        ImageTask(prompt, "in the style pencil artwork")
//    }
//    ImageGenerator.generate(tasks, "openai/output/images")
//}

//fun main() {
//    val inputFileName = "katzenpflege"
//    val paas = try {
//        readCsv("openai/$inputFileName.csv")
//            .distinctBy { it.title }
//            //.distinctBy { it.text }
//            .toList()
//    } catch (e: Exception) {
//        throw e
//    }
//
//    val tasks = paas.map { ImageTask(it.title, "Schwarz-Weiß-Bleistiftbild") }.shuffled().take(5)
//
//    ImageGenerator.generate(tasks, "openai/output/images2")
//}

//fun main() {
//    println(getRandomWPURL("openai/output/images", "katze101.com", "2023/02"))
//}

//fun main() {
//    val imageURL =
//        getImageURL(ImageRequest("Katze im Thema: \"Welche Art von Spielzeug hilft, das Kratzverhalten zu reduzieren\""))
//    println(imageURL)
//}

fun main() {
    val keyword = "Welche Art von Spielzeug hilft, das Kratzverhalten zu reduzieren"
    val keywordFileName = filenameRegex.replace(keyword, "_")

    val generateTask = ImageGenerateTask(
        "Katze im Thema: \"$keyword\"",
        "$keyword"
    )
    ImagesProcessor.generate(listOf(generateTask), "openai/experiments")

    val pngFileName =
        File("openai/experiments").listFiles().find { it.name.contains(keywordFileName) }?.absolutePath
    saveImageToFile(
        convertToRGBA(pngFileName!!)!!,
        File("openai/experiments/${keywordFileName}_rgba")
    )

    val editTask = ImageEditTask(
        File("openai/experiments/${keywordFileName}_rgba"),
        "Schwarz-Weiß-Bleistiftbild"
    )
    ImagesProcessor.edit(listOf(editTask), "openai/experiments")
}

fun downloadFile(url: URL, outputFileName: String) {
    url.openStream().use {
        Channels.newChannel(it).use { rbc ->
            FileOutputStream(outputFileName).use { fos ->
                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
            }
        }
    }
}

fun getRandomWPURL(imagesFolder: String, domain: String, date: String): String {
    val randomFile = File(imagesFolder).listFiles().random().name
    return createWPURL(domain, date, randomFile)
}

// https://katze101.com/wp-content/uploads/2023/02/enthusiastic_bengal_cat__1677512158682.png
private fun createWPURL(domain: String, date: String, imageFileName: String) =
    "https://${domain}/wp-content/uploads/${date}/$imageFileName"

fun getImageURL(imageRequest: ImageRequest): String {
    var result = ""
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build();

        println("Generating image: ${imageRequest.prompt}")
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(RequestBody.create(JSON, gson.toJson(imageRequest)))
            .header("Authorization", "Bearer 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")
            .build()
        val body = httpClient.newCall(request).execute().body?.string()
        val completionRespone = gson.fromJson<ImageResponse>(body, object : TypeToken<ImageResponse>() {}.type)
        result = completionRespone.data.firstOrNull()?.url ?: ""
    } catch (e: Exception) {
        println("Image generation error: ${e.message}")
    } finally {
        return result
    }
}

fun updateImage(file: File, prompt: String): String {
    var result = ""
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()

        println("Editing image: $${file.name}")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody("image/png".toMediaTypeOrNull()))
            .addFormDataPart("prompt", prompt)
            .addFormDataPart("n", "1")
            .addFormDataPart("size", "512x512")
            .build()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/edits")
            .post(requestBody)
            .header("Authorization", "Bearer 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")
            .build()

        val body = httpClient.newCall(request).execute().body?.string()
        val completionRespone = gson.fromJson<ImageResponse>(body, object : TypeToken<ImageResponse>() {}.type)
        result = completionRespone.data.firstOrNull()?.url ?: ""
    } catch (e: Exception) {
        println("Image generation error: ${e.message}")
    } finally {
        return result
    }
}

fun convertToRGBA(pngFileName: String): BufferedImage? {
    val pngFile = File(pngFileName)
    if (!pngFile.exists()) {
        println("File $pngFileName does not exist")
        return null
    }

    val image = ImageIO.read(pngFile)
    val rgbaImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)

    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val pixel = image.getRGB(x, y)
            val alpha = (pixel shr 24) and 0xFF
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF
            val rgbaPixel = Color(red, green, blue, alpha).rgb
            rgbaImage.setRGB(x, y, rgbaPixel)
        }
    }
    return rgbaImage
}

fun saveImageToFile(image: BufferedImage, file: File, format: String = "png") {
    ImageIO.write(image, format, file)
}

class ImagesProcessor {

    companion object {
        fun generate(tasks: List<ImageGenerateTask>, outputFolder: String) {
            for (task in tasks) {
                val imageURL = getImageURL(ImageRequest(task.keyword))
                val outputFileName = filenameRegex.replace(task.outputFileName, "_") + "_" + System.currentTimeMillis()
                downloadFile(URL(imageURL), "$outputFolder/$outputFileName.png")
                imagesGenerated.incrementAndGet()
                printCosts()
            }
        }

        fun edit(tasks: List<ImageEditTask>, outputFolder: String) {
            for (task in tasks) {
                val imageURL = updateImage(task.file, task.prompt)
                downloadFile(URL(imageURL), "$outputFolder/${task.file}_u.png")
                imagesGenerated.incrementAndGet()
                printCosts()
            }
        }
    }

}

data class ImageGenerateTask(val keyword: String, val outputFileName: String)

data class ImageEditTask(val file: File, val prompt: String)

data class ImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512"
)