package com.kishlaly.ta

import com.kishlaly.ta.openai.flow.chunked
import com.kishlaly.ta.openai.flow.encodeURL

val text = """
    und informativ

    Wenn eine Katze sich wohl fühlt, kann man normalerweise einige Anzeichen beobachten. Einige der häufigsten Anzeichen dafür, dass Ihre Katze sich wohl fühlt, sind zum Beispiel: 

    - Eine Katze, die sich wohl fühlt, ist oft ruhig und entspannt. Sie schläft viel und schläft gerne in ihrem Lieblingsplatz. Sie miaut nicht viel oder versucht nicht ständig Aufmerksamkeit zu erregen. 

    - Eine glückliche Katze zeigt oft ein Verhalten, das als „Kopfkratzen“ bekannt ist. Dies bedeutet normalerweise, dass Ihr Haustier sehr zufrieden ist und seinen Körper an Ihnen reiben möchte. Es ist auch ein Zeichen der Zuneigung von ihnen an Sie! 

    - Wenn eine Katze sich wohlfühlt, neigt sie auch dazu mehr zu spielen als sonst. Obwohl es keine Garantie gibt, dass Ihr Haustier gerne spielt (es kommt immer auf die Persönlichkeit jeder Katze an), neigen glückliche Tiere normalerweise mehr zum Spielen als unglückliche Tiere. Wenn Ihre Katze also gerne mit Spielbällen oder anderen Spielgeräten spielt oder gerne mit den Fingern gespielt wird - es ist ein gutes Zeichen! 

    All diese Verhaltensweisen deuten normalerweise darauf hin, dass Ihr Haustier glücklich und zufrieden ist! Es liegt also an Ihnen als Besitzer herauszufinden was am besten funktioniert um die Bedürfnisse Ihrer Katzen zu erfülllen und ihn so glücklich wie möglich machen!
""".trimIndent()

fun main() {
    val it = "- Eine"
    val res = if (it.substring(0, 2) == "- ") it.substring(2, it.length) else it
    println(res)
}