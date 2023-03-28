package com.kishlaly.ta.openai.flow

import com.kishlaly.ta.openai.*
import com.kishlaly.ta.openai.flow.blogpost.globalBlogTopic
import com.kishlaly.ta.openai.flow.blogpost.globalLanguage
import com.kishlaly.ta.openai.flow.blogpost.globalType
import java.io.File
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

val disclosureGlobal = "Disclosure: This article may contain affiliate links, which means that if you click on one of the product links, I may receive a commission."

val historySubtitlesEN = listOf(
    "History",
    "Historical Background",
    "A Look Back",
    "A Brief History",
    "Historical Evolution",
    "Understanding Origins",
    "The Historical Context",
    "Historical Perspective",
    "Exploring the Past",
    "The Historical Setting",
    "In Retrospect",
    "A Condensed History",
    "The Evolution of History",
    "Tracing Origins",
    "The Context of History",
    "A Historical Viewpoint"
)

val historySubtitlesDE = listOf(
    "Historischer Hintergrund",
    "Ein Rückblick",
    "Eine kurze Geschichte",
    "Historische Evolution",
    "Ursprünge verstehen",
    "Der historische Kontext",
    "Historischer Blickwinkel",
    "Die Erkundung der Vergangenheit",
    "Der historische Hintergrund",
    "Im Rückblick",
    "Eine zusammengefasste Geschichte",
    "Die Entwicklung der Geschichte",
    "Die Spurensuche nach Ursprüngen",
    "Der Kontext der Geschichte",
    "Eine historische Perspektive"
)

val factsSubtitlesEN = listOf(
    "Facts",
    "Key Facts",
    "Little-Known Facts",
    "Facts and Figures",
    "Beyond the Myths and Rumors",
    "Interesting Insights",
    "The Lesser-Known Side",
    "The Facts You Need to Know",
    "Revealing the Facts",
    "The Incredible Facts",
    "Separating Fact from Fiction",
    "The Real Story Behind",
    "Mind-Blowing Facts",
    "Myths vs. Facts",
    "The Unbelievable Facts",
    "Uncovering the Facts",
    "Surprising Facts and Stats",
    "The Essential Facts"
)

val factsSubtitlesDE = listOf(
    "Fakten",
    "Wichtige Fakten",
    "Wenig bekannte Fakten",
    "Fakten und Zahlen",
    "Jenseits von Mythen und Gerüchten",
    "Interessante Einblicke",
    "Die weniger bekannte Seite",
    "Die Fakten, die Sie kennen müssen",
    "Die Fakten enthüllen",
    "Die unglaublichen Fakten",
    "Tatsachen von Fiktion trennen",
    "Die wahre Geschichte dahinter",
    "Unglaubliche Fakten",
    "Mythen vs. Fakten",
    "Die unglaublichen Fakten",
    "Die Fakten aufdecken",
    "Überraschende Fakten und Statistiken",
    "Die wesentlichen Fakten"
)

val anotherOpitionSubtitlesEN = listOf(
    "Alternative Views",
    "The Other Side of the Story",
    "What Others Are Saying About",
    "Exploring Different Opinions",
    "Another Angle",
    "Challenging Assumptions",
    "Exploring Different Perspectives",
    "Diving into Different Opinion",
    "The Alternative Opinion",
    "What Else You Should Know",
    "The Other Side",
    "The Unpopular Opinion",
    "Why Some Disagree: Alternative Opinions",
    "The Flip Side",
    "Alternative Perspectives",
    "Examining Opposing Views",
    "Different Voices",
    "Exploring Multiple Points of View",
    "An Unconventional Take",
    "Challenging Common Beliefs",
    "Exploring Different Angles",
    "Delving into Alternative Opinions",
    "A Different Perspective",
    "Important Considerations",
    "Considering Another Perspective",
    "Unpopular Ideas",
    "Disagreeing with the Mainstream View",
    "Another Way of Looking at It"
)

val anotherOpitionSubtitlesDE = listOf(
    "Alternative Ansichten",
    "Die andere Seite der Geschichte",
    "Was andere sagen über",
    "Unterschiedliche Meinungen erkunden",
    "Eine andere Perspektive",
    "Annahmen herausfordern",
    "Unterschiedliche Perspektiven erkunden",
    "Vertiefung in unterschiedliche Meinungen",
    "Die alternative Meinung",
    "Was Sie sonst noch wissen sollten",
    "Die andere Seite",
    "Die unbeliebte Meinung",
    "Warum manche anderer Meinung sind: alternative Meinungen",
    "Die Kehrseite der Medaille",
    "Alternative Perspektiven",
    "Untersuchung gegensätzlicher Ansichten",
    "Verschiedene Standpunkte",
    "Erkundung mehrerer Blickwinkel",
    "Eine unkonventionelle Sichtweise",
    "Herausforderung gängiger Überzeugungen",
    "Erkundung unterschiedlicher Ansätze",
    "Eintauchen in alternative Meinungen",
    "Eine andere Perspektive",
    "Wichtige Überlegungen",
    "Berücksichtigung einer anderen Perspektive",
    "Unbeliebte Ideen",
    "Widerspruch zur gängigen Meinung",
    "Eine andere Art der Betrachtung"
)

val personalExperienceOpitionSubtitlesEN = listOf(
    "My Thoughts",
    "In My Opinion",
    "My Personal Perspective",
    "From My Perspective",
    "A Personal Look",
    "How I See It",
    "My Interpretation",
    "The Way I See It",
    "A Subjective View",
    "My Feelings About That",
    "My Position",
    "My Experience",
    "Reflecting on My Experience",
    "My Personal Story",
    "An Inside Look",
    "My Point of View",
    "A Personal Reflection",
    "The Lessons I Learned",
    "My Reflections",
    "In My View",
    "My Unique Perspective",
    "Through My Eyes",
    "A Personal Examination",
    "My Take on It",
    "My Understanding",
    "My Personal Outlook",
    "My Emotional Response",
    "Where I Stand",
    "My Encounter",
    "Looking Back on My Experience",
    "My Personal Account",
    "A Behind-the-Scenes Look",
    "My Personal Insight",
    "A Personal Assessment",
    "My Considerations",
    "Personal Takeaways"
)

val personalExperienceSubtitlesDE = listOf(
    "Meine Gedanken",
    "In meiner Meinung",
    "Meine persönliche Perspektive",
    "Aus meiner Perspektive",
    "Ein persönlicher Blick",
    "Wie ich es sehe",
    "Meine Interpretation",
    "Die Art und Weise, wie ich es sehe",
    "Eine subjektive Sichtweise",
    "Meine Gefühle dazu",
    "Meine Position",
    "Meine Erfahrung",
    "Rückblick auf meine Erfahrung",
    "Meine persönliche Geschichte",
    "Ein Blick hinter die Kulissen",
    "Mein Standpunkt",
    "Eine persönliche Reflexion",
    "Die Lektionen, die ich gelernt habe",
    "Meine Betrachtungen",
    "In meiner Ansicht",
    "Meine einzigartige Perspektive",
    "Durch meine Augen",
    "Eine persönliche Untersuchung",
    "Meine Meinung dazu",
    "Mein Verständnis",
    "Mein persönlicher Ausblick",
    "Meine emotionale Reaktion",
    "Wo ich stehe",
    "Meine Begegnung",
    "Rückblick auf meine Erfahrung",
    "Mein persönlicher Bericht",
    "Ein Blick hinter die Kulissen",
    "Meine persönliche Einsicht",
    "Eine persönliche Bewertung",
    "Meine Überlegungen",
    "Persönliche Erkenntnisse"
)

val conclusionSubtitlesEN = listOf(
    "Conclusion",
    "Key Takeaways",
    "Final Thoughts",
    "Wrap-up",
    "In summary",
    "Closing Remarks",
    "Lessons Learned",
    "The Bottom Line",
    "Food for Thought",
    "The Takeaway",
    "Main Points",
    "Final Reflections",
    "Summary",
    "Recap",
    "Closing Notes",
    "Key Insights",
    "Key Messages",
    "Implications and Recommendations",
    "Future Considerations",
    "Lessons to Remember",
    "Takeaways"
)

val conclusionSubtitlesDE = listOf(
    "Schlussfolgerung",
    "Wichtige Erkenntnisse",
    "Letzte Gedanken",
    "Zusammenfassung",
    "In Kurzform",
    "Schließende Bemerkungen",
    "Gelernte Lektionen",
    "Das Wichtigste",
    "Zum Nachdenken anregen",
    "Die Kernbotschaft",
    "Hauptpunkte",
    "Abschließende Gedanken",
    "Zusammenfassung",
    "Rückblick",
    "Abschließende Hinweise",
    "Wichtige Erkenntnisse",
    "Wichtige Botschaften",
    "Folgerungen und Empfehlungen",
    "Zukünftige Überlegungen",
    "Lektionen zum Erinnern",
    "Wichtige Punkte"
)

fun getReadAlsoTitle() = when (globalLanguage) {
        Language.DE -> "Lesen Sie auch:"
        Language.EN -> "Read also:"
    }

fun getHistorySubtitle() = when (globalLanguage) {
        Language.DE -> historySubtitlesDE.shuffled().random()
        Language.EN -> historySubtitlesEN.shuffled().random()
    }

fun getFactsSubtitle() = when (globalLanguage) {
        Language.DE -> factsSubtitlesDE.shuffled().random()
        Language.EN -> factsSubtitlesEN.shuffled().random()
    }

fun getAnotherOpitonSubtitle() = when (globalLanguage) {
        Language.DE -> anotherOpitionSubtitlesDE.shuffled().random()
        Language.EN -> anotherOpitionSubtitlesEN.shuffled().random()
    }

fun getPersonalExperienceSubtitle() = when (globalLanguage) {
        Language.DE -> personalExperienceSubtitlesDE.shuffled().random()
        Language.EN -> personalExperienceOpitionSubtitlesEN.shuffled().random()
    }

fun getConclusionSubtitle() = when (globalLanguage) {
        Language.DE -> conclusionSubtitlesDE.shuffled().random()
        Language.EN -> conclusionSubtitlesEN.shuffled().random()
    }

fun getPromptsMarkers(): List<String> {
    return when (globalLanguage) {
        Language.DE -> listOf(
            "Ich schreibe einen Artikel über",
            "Schreiben Sie eine ausführliche Einführung zu diesem Artikel",
            "Schreiben Sie eine Liste mit",
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
            "Write a list of",
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
    EXTERNAL_PROMPT(
        mapOf(
            Language.DE to "",
            Language.EN to ""
        )
    ),
    TOC_PLAN(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über ${globalBlogTopic}. Das Thema ist: \"###param###\". Schreiben Sie eine Liste mit 10 bis 15 kurzen Unterüberschriften.",
            Language.EN to "I am writing an article about ${globalBlogTopic}. The title of the article is: \"###param###\". Write a list of 10 to 15 short subheadings."
        )
    ),
    TOC_PLAN_SAVO(
        mapOf(
            Language.DE to "Ich schreibe einen Artikel über ${globalBlogTopic}. Das Thema ist: \"###param###\". Schreiben Sie eine Liste mit 4 bis 7 kurzen Unterüberschriften.",
            Language.EN to "I'm writing an article about \"${globalBlogTopic}\". Write a list of 5 to 7 short subheadings. At the end of the article, I will place an advertisement, so these subheadings should smoothly lead to this, revealing the main topic."
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
    FEATURED_IMAGE_TASK(
        mapOf(
            Language.DE to "Create a prompt that gives an illustrator a task to depict a picture for blog article about \"###param###\"",
            Language.EN to "Create a prompt that gives an illustrator a task to depict a picture for blog article about \"###param###\"",
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
            println("[$globalType][$type][$intent]")
            when (type) {
                Type.TEXT -> {

                    val outputFileName = "${intent}_${index + 1}"
                    if (File("$folder/$outputFileName").exists()) {
                        if (File("$folder/$outputFileName").readText().trim().isNotEmpty()) {
                            println("$folder/$outputFileName exists. Skipping...")
                            return@forEachIndexed
                        }
                    }

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