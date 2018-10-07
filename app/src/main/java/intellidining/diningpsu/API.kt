package intellidining.diningpsu

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URL

typealias Callback<T> = (data: T) -> Unit

object API {
    val base = "https://api.absecom.psu.edu/rest/"
    val mapper = jacksonObjectMapper()
    val handler = Handler(Looper.getMainLooper())

    inline fun <reified T> get(endpoint: String, data: HashMap<String, Any>?, crossinline callback: Callback<List<T>>) {
        AsyncTask.execute {
            val query: String = if (data != null) {
                "?" + data.map { it.key + '=' + it.value }.joinToString("&")
            } else {
                ""
            }

            val url = URL("$base$endpoint/v1/221723$query")
            val rawData = url.readText()
            val json: HashMap<String, Array<Any>> = mapper.readValue(rawData, object : TypeReference<HashMap<String, Array<Any>>>() {})
            val zipped = json["DATA"]?.map { datum -> json["COLUMNS"]?.mapIndexed { i, k -> k.toString() to (datum as List<Any>)[i] }?.toMap() }

            val processed = zipped?.map { rename(it!!) }

            handler.post {
                println(processed)

                val type = TypeFactory.defaultInstance().constructCollectionType(List::class.java, T::class.java)

                val value: List<T> = mapper.convertValue(processed, type)

                println(value)

                callback(value)
            }
        }
    }

    fun getCampuses(callback: Callback<List<Campus>>) {
        get("facilities/campuses", null, callback)
    }

    fun getDiningHalls(callback: Callback<List<DiningHall>>) {
        get("facilities/areas", null, callback)
    }

    fun getHours(callback: Callback<DiningHallHours>) {
        get<LocationHours>("facilities/hours", null) { data ->
            callback(data.mapNotNull { it.menuCategoryNumber }.map { k -> k to data.filter { it.menuCategoryNumber === k } }.toMap())
        }
    }

    fun getLocations(location: String, callback: Callback<List<Location>>) {
        get<Location>("facilities/locations", hashMapOf("location" to location), callback)
    }

    fun getMenu(date: String, location: String, callback: Callback<Menu>) {
        get<MenuItem>("services/food/menus", hashMapOf("date" to date, "location" to location)) { data ->
            val meals = data.map { datum -> datum.mealName }.distinct()
            val sections = data.map { datum -> datum.menuCategoryName }.distinct()

            callback(meals
                    .map { meal ->
                        meal to sections.map { section ->
                            section to data.filter { datum ->
                                datum.mealName == meal && datum.menuCategoryName == section
                            }
                        }.filter { it.second.isNotEmpty() }
                                .toMap()
                    }
                    .toMap())
        }
    }

    fun rename(input: Map<String, Any>): Map<String, Any> {
        return input.mapKeys { toCamelCase(it.key) }
    }

    private fun toCamelCase(input: String): String {
        return input.toLowerCase().split('_').asSequence().mapIndexed { i, str -> if (i > 0) str[0].toUpperCase() + str.substring(1) else str }.joinToString("")
    }
}