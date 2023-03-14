package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.*
import com.kishlaly.ta.openai.flow.blogpost.globalBlogTopic
import com.kishlaly.ta.openai.flow.blogpost.globalLanguage
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

enum class Language {
    DE,
    EN
}

enum class Type {
    TEXT,
    IMAGE
}

fun getReadAlsoTitle() = when (globalLanguage) {
        Language.DE -> "Lesen Sie auch:"
        Language.EN -> "Read also:"
    }

fun getPromptsMarkers(): List<String> {
    return when (globalLanguage) {
        Language.DE -> listOf(
            "Ich schreibe einen Artikel über",
            "Schreiben Sie eine ausführliche Einführung zu diesem Artikel",
            "Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften",
            "Schreiben Sie eine lange historische Notiz zu diesem Thema",
            "Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema",
            "Begründen Sie Ihre Antwort mit einigen Beispielen",
            "Schreiben Sie interessante Fakten über dieses Thema",
            "Formatieren Sie den Text in Form von Absätzen ohne Zahlen",
            "Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist",
            "Schreiben Sie drei Absätze zu diesem Thema",
            "Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste",
            "Schreiben Sie eine historische Notiz zu diesem Thema",
            "Schreiben Sie eine ausführliche Expertenantwort",
            "Schreiben Sie von 3 bis 7 interessante Fakten"
        )
        Language.EN -> listOf(
            "I am writing an article about",
            "The title of the article is:",
            "Write a detailed introduction to this article",
            "Write a list of 10 to 15 short subheadings",
            "Write a long historical note on this topic",
            "Write a detailed expert answer on this topic",
            "Justify your answer with some examples",
            "Write interesting facts about this topic",
            "Format the text in the form of paragraphs without numbers",
            "Find a key phrase that is the opposite of this",
            "Write three paragraphs on this topic",
            "From this text, create a comma-separated list of 5",
            "Write a historical note on this topic",
            "Write from 3 to 7"
        )
    }
}

enum class Intent(val map: Map<Language, String>) {
    INTRODUCTION(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über ${globalBlogTopic}. Der Titel des Artikels lautet: \"###param###\". Schreiben Sie eine ausführliche Einführung zu diesem Artikel.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. The title of the article is: \"###param###\". Write a detailed introduction to this article."
        )
    ),
    TOC_PLAN(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über ${globalBlogTopic}. Das Thema ist: \"###param###\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. The title of the article is: \"###param###\". Write a list of 10 to 15 short subheadings."
        )
    ),
    CONTENT_PART_1_HISTORY(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie eine lange historische Notiz zu diesem Thema: \"###param###\".",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write a long historical note on this topic: \"###param###\"."
        )
    ),
    HISTORY(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie eine lange historische Notiz zu diesem Thema: \"###param###\".",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write a long historical note on this topic: \"###param###\"."
        )
    ),
    CONTENT_PART_2_MAIN(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"###param###\". Begründen Sie Ihre Antwort mit einigen Beispielen.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write a detailed expert answer on this topic: \"###param###\". Justify your answer with some examples."
        )
    ),
    MAIN(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie eine ausführliche Expertenantwort auf dieses Thema: \"###param###\". Begründen Sie Ihre Antwort mit einigen Beispielen.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write a detailed expert answer on this topic: \"###param###\". Justify your answer with some examples."
        )
    ),
    CONTENT_PART_3_FACTS(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie interessante Fakten über dieses Thema: \"###param###\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write interesting facts about this topic: \"###param###\". Format the text in the form of paragraphs without numbers."
        )
    ),
    FACTS(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie von 3 bis 7 interessante Fakten über dieses Thema: \"###param###\". Formatieren Sie den Text in Form von Absätzen ohne Zahlen.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write from 3 to 7 interesting facts about this topic: \"###param###\". Format the text in the form of paragraphs without numbers."
        )
    ),
    OPPOSITE_OPINION_QUESTION(
        mapOf(
            Language.DE to "Finden Sie einen Schlüsselsatz, der das Gegenteil davon ist: \"###param###\"",
            Language.EN to "Find a key phrase that is the opposite of this: \"###param###\""
        )
    ),
    OPPOSITE_OPINION_TEXT(
        mapOf(
            Language.DE to "Ich schreibe einen Blog über ${globalBlogTopic}. Schreiben Sie drei Absätze zu diesem Thema: \"###param###\".",
            Language.EN to "I am writing an article about ${globalBlogTopic}. Write three paragraphs on this topic: \"###param###\"."
        )
    ),
    TAGS(
        mapOf(
            Language.DE to "Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: \"###param###\"",
            Language.EN to "From this text, create a comma-separated list of 5 keywords: \"###param###\""
        )
    ),
    TAGS_PAA(
        mapOf(
            Language.DE to "Erstellen Sie aus diesem Text eine durch Kommas getrennte Liste mit 5 Schlüsselwörtern: \"###param###\"",
            Language.EN to "From this text, create a comma-separated list of 5 keywords: \"###param###\""
        )
    ),
    TOC_IMAGES(
        mapOf(
            Language.DE to "",
            Language.EN to "",
        )
    ),
    FEATURED_IMAGE(
        mapOf(
            Language.DE to "",
            Language.EN to "",
        )
    ),
    CONCLUSION(
        mapOf(
            Language.DE to "Schreiben Sie ein Fazit zu diesem Artikel: \"###param###\"",
            Language.EN to "Write a conclusion to this article: \"###param###\""
        )
    ),
    RANDOM_ADDITION(
        mapOf(
            Language.DE to "Beschreiben Sie Ihre persönliche Erfahrung zu diesem Thema: \"###param###\"|||Schreiben Sie im Auftrag der Redaktion unseres Blogs eine Stellungnahme zu diesem Thema: \"###param###\"",
            Language.EN to "Describe your personal experience on this topic: \"###param###\"|||On behalf of the editors of our blog, write a statement on this topic: \"###param###\""
        )
    );

    fun get(language: Language, paramValue: String) = map[language]!!.replace("###param###", paramValue)
}

fun getFixingPrompt() = when (globalLanguage) {
    Language.DE -> "korrigieren Sie die Tippfehler in diesem Text:"
    Language.EN -> "correct the typos in this text:"
}

fun getWritingTone() = when (globalLanguage) {
        Language.DE -> {
            val tone = listOf(
                "Objektiv",
                "Subjektiv",
                "Beschreibend",
                "Informativ",
                "Unterhaltsam",
                "Lyrisch",
                "Humorvoll",
                "Persönlich",
                "Dramatisch",
                "Kritisch"
            )
            "Antwortton - ${tone[Random.nextInt(tone.size)]}"
        }
        Language.EN -> {
            val tone = listOf(
                "objective",
                "subjective",
                "descriptive",
                "informative",
                "entertaining",
                "lyrical",
                "humorous",
                "personal",
                "dramatic",
                "critical"
            )
            "Answer tone - ${tone[Random.nextInt(tone.size)]}"
        }
    }

class Step(
    val intent: Intent,
    val input: List<String> = emptyList(),
    val folder: String,
    val postProcessings: List<(String) -> String> = emptyList(),
    val type: Type = Type.TEXT,
    val imagesCount: Int = 1,
    val useTone: Boolean = false,
    val customImageName: String = "image_${System.currentTimeMillis()}",
    val fixTypos: Boolean = false
) {
    init {
        input.forEachIndexed { index, prompt ->
            var finalPrompt = prompt
            if (useTone) {
                finalPrompt = "$prompt ${getWritingTone()}"
            }
            println("[$type][$intent]")
            when (type) {
                Type.TEXT -> {
                    var completion = ""

                    try {
                        completion = getCompletion(finalPrompt)
                    } catch (e: OpenAIException) {
                        println("!!! Got empty response. Retrying...")
                        completion = getCompletion(finalPrompt)
                    }

                    if (fixTypos) {
                        try {
                            completion = getCompletion("${getFixingPrompt()} \"$completion\"")
                        } catch (e: OpenAIException) {
                            println("!!! Got empty response. Retrying...")
                            completion = getCompletion("${getFixingPrompt()} \"$completion\"")
                        }
                    }

                    val outputFileName = "${intent}_${index + 1}"
                    Files.write(
                        Paths.get("$folder/$outputFileName"),
                        completion.toByteArray()
                    )
                }

                Type.IMAGE -> {
                    val imageGenerateTask =
                        ImageGenerateTask(
                            keyword = prompt,
                            outputFolderName = "$folder",
                            outputFileName = customImageName,
                            n = imagesCount
                        )
                    ImagesProcessor.generate(listOf(imageGenerateTask))
                }
            }
            println("")
        }
    }

    private fun getCompletion(prompt: String): String {
        val rawResponse = getCompletion(CompletionRequest(prompt = prompt))
        var finalResult = rawResponse
        postProcessings.forEach {
            finalResult = it(finalResult)
        }
        return finalResult
    }
}