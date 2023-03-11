package com.kishlaly.ta.openai

import com.google.common.reflect.TypeToken
import com.kishlaly.ta.openai.flow.timeoutRetry
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

//fun main() {
//    val tasks = (1..5).map {
//        var prompt = Combiner.combine(
//            listOf(
//                "openai/katze101/breeds",
//                "openai/katze101/age",
//                "openai/katze101/behaviour",
//                "openai/katze101/places",
//            )
//        )
//        ImageGenerateTask("${prompt}in the style pencil artwork", "openai/output/images", "${prompt}.png", 1)
//    }
//    ImagesProcessor.generate(tasks)
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

fun main() {
    val breeds = File("openai/katze101/breeds").readLines()
    val ages = File("openai/katze101/age").readLines()
    val moods = File("openai/katze101/mood").readLines()

    val tasks = mutableListOf<ImageGenerateTask>()
    (1..3).forEach {
        val breed = breeds.random()
        val age = ages.random()
        val mood = moods.random()
        tasks.add(ImageGenerateTask(
            keyword = "a close up, studio photographic portrait of a ${breed} ${age} that looks ${mood}. White background",
            outputFolderName = "openai/output/images",
            outputFileName = "katze101.com-${System.nanoTime()}",
            n = 1
        ))
    }
    ImagesProcessor.generate(tasks)
}

//fun main() {
//    val keyword = "Welche Art von Spielzeug hilft, das Kratzverhalten zu reduzieren"
//    val keywordFileName = filenameRegex.replace(keyword, "_")
//    val outputFolder = "openai/experiments"
//
//    val generateTask = ImageGenerateTask(
//        "Katze im Thema: \"$keyword\"",
//        "$keyword"
//    )
//
//    ImagesProcessor.generate(listOf(generateTask), outputFolder)
//
//    val pngFileName =
//        File(outputFolder).listFiles().find { it.name.contains(keywordFileName) }?.absolutePath
//    saveImageToFile(
//        convertToRGBA(pngFileName!!)!!,
//        File("$outputFolder/${keywordFileName}_rgba")
//    )
//
//    val editTask = ImageEditTask(
//        folder = outputFolder,
//        file = "${keywordFileName}_rgba",
//        prompt = "Schwarz-Weiß-Bleistiftbild"
//    )
//    ImagesProcessor.edit(listOf(editTask))
//}

//fun main() {
//    val variationTask = ImageVariationTask("openai", "cat.png")
//
//    ImagesProcessor.variation(listOf(variationTask))
//}


fun downloadFile(urls: List<String?>, outputFileName: String) {
    urls.filterNotNull().map { URL(it) }.forEachIndexed { index, url ->
        url.openStream().use {
            Channels.newChannel(it).use { rbc ->
                FileOutputStream("${outputFileName}_${index}.png").use { fos ->
                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                }
            }
        }
    }
}

fun getRandomWPURL(imagesFolder: String, domain: String, date: String): String {
    val randomFile = File(imagesFolder).listFiles().random().name
    return createWPURL(domain, date, randomFile)
}

private fun createWPURL(domain: String, date: String, imageFileName: String) =
    "https://${domain}/wp-content/uploads/${date}/$imageFileName"

fun getImageURLs(imageRequest: ImageRequest): List<String?> {
    var result = listOf<String?>()
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build();
        println("Generating image: ${imageRequest.prompt}")
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(RequestBody.create(JSON, gson.toJson(imageRequest)))
            .header("Authorization", "Bearer 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")
            .build()
        val body = timeoutRetry(httpClient, request)
        if (body == null) {
            throw RuntimeException("Didn't make it after 3 retries :(")
        }
        val completionRespone = gson.fromJson<ImageResponse>(body, object : TypeToken<ImageResponse>() {}.type)
        result = completionRespone.data.map { it.url }.toList()
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

        println("Editing image: ${file.name}")

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

fun variationImage(file: File): String {
    var result = ""
    try {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()

        println("Editing image: ${file.name}")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody("image/png".toMediaTypeOrNull()))
            .addFormDataPart("size", "512x512")
            .addFormDataPart("user", "vladimir@kishlaly.com")
            .build()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/variations")
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

fun convertJpgToPng(jpgFile: File, pngFile: File) {
    val jpegImage: BufferedImage = ImageIO.read(jpgFile)
    val pngImage: BufferedImage = BufferedImage(jpegImage.width, jpegImage.height, BufferedImage.TYPE_INT_ARGB)
    val graphics = pngImage.createGraphics()
    graphics.drawImage(jpegImage, 0, 0, null)
    graphics.dispose()
    ImageIO.write(pngImage, "png", pngFile)
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

        fun generate(tasks: List<ImageGenerateTask>) {
            for (task in tasks) {
                generateOneImage(task)
            }
        }

        fun generateMultithreaded(tasks: List<ImageGenerateTask>, threads: Int = 5) {
            val executor = Executors.newFixedThreadPool(threads)
            for (task in tasks) {
                executor.submit {
                    generateOneImage(task)
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }

        private fun generateOneImage(task: ImageGenerateTask) {
            val imageURLs = getImageURLs(ImageRequest(task.keyword, task.n, task.size))
            downloadFile(imageURLs, "${task.outputFolderName}/${task.outputFileName}")
            imagesGenerated.addAndGet(imageURLs.size)
            printCosts()
        }

        fun edit(tasks: List<ImageEditTask>) {
            for (task in tasks) {
                val imageURL = updateImage(File("${task.folder}/${task.file}"), task.prompt)
                downloadFile(listOf(imageURL), "${task.folder}/${task.file}_u.png")
                imagesGenerated.incrementAndGet()
                printCosts()
            }
        }

        fun variation(tasks: List<ImageVariationTask>) {
            for (task in tasks) {
                val imageURL = variationImage(File("${task.folder}/${task.file}"))
                downloadFile(listOf(imageURL), "${task.folder}/${task.file}_v.png")
                imagesGenerated.incrementAndGet()
                printCosts()
            }
        }
    }

}

data class ImageGenerateTask(
    val keyword: String,
    val outputFolderName: String,
    val outputFileName: String,
    val n: Int,
    val size: String = "512x512"
)

data class ImageEditTask(
    val folder: String,
    val file: String,
    val prompt: String
)

data class ImageVariationTask(
    val folder: String,
    val file: String
)

data class ImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "512x512",
    val user: String = "vladimir@kishlaly.com"
)