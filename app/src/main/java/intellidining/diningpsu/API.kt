package intellidining.diningpsu

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URL

typealias Callback<T> = (data: T) -> Unit

object API {
    val base = "http://api.absecom.psu.edu/rest/"
    val mapper = jacksonObjectMapper()
    val handler = Handler(Looper.getMainLooper())

    fun <T> get(endpoint: String, data: HashMap<String, Any>?, callback: Callback<Array<T>>) {
        AsyncTask.execute {
            val query: String = if (data != null) {
                "?" + data.map { it.key + '=' + it.value }.joinToString("&")
            } else {
                ""
            }

            val url = URL("$base$endpoint/v1/221723$query")
            val rawData = url.readText()
            val json: HashMap<String, Array<Any>> = mapper.readValue(rawData, object: TypeReference<HashMap<String, Array<Any>>>() {})
            val zipped = json["DATA"]?.map {datum -> json["COLUMNS"]?.mapIndexed { i, k -> k.toString() to (datum as Array<Any>)[i] }?.toMap()}

            handler.post {
                callback(mapper.convertValue(zipped?.map { rename(it!!) }, object : TypeReference<Array<T>>() {}))
            }
        }
    }

    fun getCampuses(callback: Callback<Array<Campus>>) {
        get("facilities/campuses", null, callback)
    }

    fun getDiningHalls(callback: Callback<Array<DiningHall>>) {
        get("facilities/areas", null, callback)
    }

    fun getHours(callback: Callback<DiningHallHours>) {
        get<LocationHours>("facilities/hours", null) { data ->
            callback(data.mapNotNull { it.menuCategoryNumber }.map{ k -> k to data.filter {it.menuCategoryNumber === k}}.toMap())
        }
    }

    fun getLocations(location: String, callback: Callback<Array<Location>>) {
        get<Location>("facilities/locations", hashMapOf("location" to location), callback)
    }

    fun getMenu(date: String, location: String, callback: Callback<Menu>) {
        get<MenuItem>("services/food/menus", hashMapOf("date" to date, "location" to location)) { data ->
            val meals = data.map {datum -> datum.mealName}.distinct()
            val sections = data.map {datum -> datum.menuCategoryName}.distinct()

            callback(meals.map { k -> k to sections.map { k1 -> k1 to data.filter{ datum -> datum.mealName === k && datum.menuCategoryName === k1} }.toMap() }.toMap())
        }
    }

    private fun rename(input: Map<String, Any>): Map<String, Any> {
        return input.mapKeys {toCamelCase(it.key)}
    }

    private fun toCamelCase(input: String): String {
        return input.toLowerCase().split('_').asSequence().mapIndexed { i, str -> if (i > 0) str[0].toUpperCase() + str.substring(1) else str}.joinToString("")
    }
}