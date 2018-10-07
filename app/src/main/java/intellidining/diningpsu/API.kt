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
            val json: HashMap<String, Array<Array<Any>>> = mapper.readValue(rawData, object: TypeReference<HashMap<String, Array<Any>>>() {})
            val zipped = json["DATA"]?.map {datum: Array<Any> -> json["COLUMNS"]?.foldIndexed(HashMap<String, Any>()) { i, acc, k -> acc[k.toString()] = datum[i]; acc}}

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
            callback(data.mapNotNull { it.menuCategoryNumber }.fold(DiningHallHours()) { acc, k -> acc[k] = data.filter {it.menuCategoryNumber === k}; acc })
        }
    }

    fun getLocations(location: String, callback: Callback<Array<Location>>) {
        get<Location>("facilities/locations", hashMapOf("location" to location), callback)
    }

    fun getMenu(date: String, location: String, callback: Callback<Menu>) {
        get<MenuItem>("services/food/menus", hashMapOf("date" to date, "location" to location)) { data ->
            val mealNames = data.map {datum -> datum.mealName}
            val meals = mealNames.filterIndexed {index, value -> mealNames.indexOf(value) == index}

            val sectionNames = data.map {datum -> datum.menuCategoryName}
            val sections = sectionNames.filterIndexed {index, value -> sectionNames.indexOf(value) == index}

            callback(meals.fold(Menu()) {obj, k -> obj[k] = sections.fold(HashMap()) { obj1, k1 -> obj1[k1] = data.filter{ datum -> datum.mealName === k && datum.menuCategoryName === k1}; obj1}; obj})
        }
    }

    private fun rename(input: HashMap<String, Any>): Map<String, Any> {
        return input.mapKeys {toCamelCase(it.key)}
    }

    private fun toCamelCase(input: String): String {
        return input.toLowerCase().split('_').asSequence().mapIndexed { i, str -> if (i > 0) str[0].toUpperCase() + str.substring(1) else str}.joinToString("")
    }
}