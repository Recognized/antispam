@file:JvmName("MainKt")

package antispam

import org.apache.lucene.morphology.english.EnglishLuceneMorphology
import org.apache.lucene.morphology.russian.RussianLuceneMorphology
import org.jsoup.Jsoup
import java.io.File

private val english = EnglishLuceneMorphology()
private val russian = RussianLuceneMorphology()

fun reformat(file: File, out: File) {
    out.bufferedWriter().use { writer ->
        readFile(file).forEach { (spam, bytes, url) ->
            writer.write(
                "$spam\t${Jsoup.parse(
                    String(
                        bytes,
                        Charsets.UTF_8
                    ), url
                ).text().words().joinToString(separator = " ").takeIf { it.isNotBlank() } ?: "_"}\n"
            )
        }
    }
}

fun reformatTest(file: File, out: File) {
    val data = readFile(file).map { (_, bytes, url) ->
        Jsoup.parse(
            String(
                bytes,
                Charsets.UTF_8
            ), url
        ).text().words().joinToString(separator = " ").takeIf { it.isNotBlank() } ?: "_"
    }.toList()
    out.bufferedWriter().use { writer ->
        readIds(file).withIndex().forEach {
            writer.write("${it.value}\t${data[it.index]}\n")
        }
    }
}

fun main(args: Array<String>) {
    val (trainFile, testFile, tempTrain, tempTest) = args[0].split(',').map { File(it) }
    reformat(trainFile, tempTrain)
    reformatTest(testFile, tempTest)
}

fun readIds(file: File): Sequence<String> = sequence {
    file.bufferedReader().use { reader ->
        for (line in reader.lineSequence().drop(1)) {
            val (id, _, _, _) = line.split('\t')
            yield(id)
        }
    }
}

data class D(val klass: Int, val bytes: ByteArray, val baseUrl: String, val id: Long)

fun readFile(file: File): Sequence<D> = sequence {
    file.bufferedReader().use { reader ->
        val decoder = java.util.Base64.getDecoder()
        for (line in reader.lineSequence().drop(1)) {
            val (id, klass, url, html) = line.split('\t')
            yield(D(klass.toInt(), decoder.decode(html), url, id.toLong()))
        }
    }
}

private val WORDS_REGEX = "\\b.+\\b".toRegex()

fun String.words(): List<String> {
    return WORDS_REGEX.findAll(this).mapNotNull { result ->
        result.value.toLowerCase().replace('\t', ' ').replace('\n', ' ')
//            .let {
//            when {
//                it.first().isDigit() -> {
//                    val x = it.toLongOrNull()
//                    when {
//                        x == null -> null
//                        x < 0 -> "-1"
//                        x <= 10L -> x.toString()
//                        x <= 100L -> "100"
//                        x <= 1000L -> "1000"
//                        x <= 1000000 -> "1000000"
//                        else -> null
//                    }
//                }
//                english.checkString(it) -> english.getNormalForms(it).first()
//                russian.checkString(it) -> russian.getNormalForms(it).first()
//                else -> null
//            }
//        }
    }.filter { it.isNotBlank() }.toList()
}