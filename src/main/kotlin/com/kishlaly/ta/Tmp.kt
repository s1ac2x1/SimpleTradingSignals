package com.kishlaly.ta

import com.kishlaly.ta.openai.flow.Intent
import com.kishlaly.ta.openai.flow.createParagraphs
import com.kishlaly.ta.openai.mainOutputFolder
import java.io.File

val text = """
    Die Geschichte des Kratzens ist eine der ältesten menschlichen Beziehungen zu Katzen. Seit Jahrhunderten haben Menschen versucht, die natürliche Anziehungskraft zwischen Katzen und Menschen zu erforschen. Kratzen ist ein natürliches Verhalten bei Katzen, das sie als Mittel verwenden, um ihre Umgebung zu markieren und ihr Territorium abzustecken. 

    Es wird angenommen, dass es für sie auch eine Art von Entspannung und Stressabbau sein kann. Einige Forscher glauben sogar, dass Kratzen Teil der natürlichen sozialen Interaktion zwischen Mensch und Tier sein könnte. In den frühesten Aufzeichnungen über Katzen wurde berichtet, dass Kratzer als Geschenke an die Götter geschickt wurden - als Zeichen des Respekts vor dem mystischen Wesen der Katze. 

    Dieser Brauch existiert noch heute in vielerlei Form in vielerlei Ländern auf der ganzen Welt. Kratzer haben auch eine lange Geschichte im Umgang mit Haustieren: Sie wurden verwendet, um den Tieren beizubringen, wo sie schlafen oder essen sollten; um ihnen beizubringen, wo sie ihr Geschäft machen sollten; oder man benutzte sie als Mittel der Bestrafung für unerwünschtes Verhalten. Heutzutage versteht man jedoch besser die Bedeutung von Kratzern für die psychische und emotionale Gesundheit von Haustieren - sowohl beim Menschen als auch beim Tier - und es gibt spezielle Produkte auf dem Markt (Kratzbäume usw. 

    ), die diesem Zweck dienlich sein könnnen. Obwohl es schwer ist zu bestimmen, warum Katzen kratzen oder warum Menschen angezogen werden von diesem Verhalten – was immer der Grund sein mag – bleibt Kratzen eines der älteststen Beispiele für den tiefgreifendsten Teil unserer Beziehung mit unseren geliebten Fellfreundinnnen: Das Verständnis und Respekt voreinander!.
""".trimIndent()

fun main() {
//    val screen2 = CacheReader.getSymbolData(TaskType.THREE_DISPLAYS_BUY.getTimeframeIndicators(2), "LUMN")
//    println(screen2.lastQuote.nativeDate)
//    val efi: List<ElderForceIndex> = screen2.indicators.get(Indicator.EFI) as List<ElderForceIndex>
//    println(efi)

//    val listFiles = File("openai/output/images").listFiles()
//    File("openai/output/text/").listFiles().forEach {
//        val text = it.readText()
//        val imageUrl = listFiles.random().name
//        val corrected = text.replace(".DS_Store", imageUrl)
//        it.writeText(corrected)
//    }

//    val content = File("openai/katzenverhalten.xml").readText()
//    val newContent = content.replace("<content>.\n\n", "<content>")
//    File("openai/katzenverhalten2.xml").writeText(newContent)

    val withoutLineBreaks = text.replace("\n", "")
    println(withoutLineBreaks)

    val headingContent = StringBuilder()
    listOf(Intent.CONTENT_PART_1_HISTORY, Intent.CONTENT_PART_2_MAIN, Intent.CONTENT_PART_3_FACTS).shuffled()
        .forEach { intent ->
            val part =
                File("openai/flow/output/1").listFiles().find { file -> file.name.contains("${intent.name}_1") }
                    ?.readText() ?: ""
            headingContent.append(part)
        }
    val res = createParagraphs(headingContent.toString())
    println(res)
}
