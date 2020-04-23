package antispam

import org.jsoup.Jsoup
import java.io.File
import java.util.*

fun main() {
    readFile(File("C:/Temp/kaggle_test_data_tab.csv")).forEach {
        Jsoup.parse(String(it.bytes, Charsets.UTF_8)).body()?.text().orEmpty().words()
    }?.let {
//        println(
//            it.id
//        )
//        println(String(it.bytes, Charsets.UTF_8))
    }
}

