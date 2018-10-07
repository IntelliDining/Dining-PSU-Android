package intellidining.diningpsu

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiningHall(val name: String, val imageIcon: Uri? = null): Parcelable

@Parcelize
data class Location(val menuCategoryNumber: String, val menuCategoryName: String, val locationNumber: String, val locationName: String): Parcelable

@Parcelize
data class LocationHours(val menuCategoryNumber: String?, val dayOfWeekStart: Int, val dayOfWeekEnd: Int?, val timeOpen: String, val timeClosed: String): Parcelable

typealias DiningHallHours = Map<String, List<LocationHours>>

@Parcelize
data class Campus(val campusCode: String, val campusName: String, val rssLink: String): Parcelable

typealias Menu = Map<String, Map<String, List<MenuItem>>>

@Parcelize
data class MenuItem(val id: Int, val serveDate: String, val mealNumber: Int, val mealName: String, val locationNumber: Int, val locationName: String, val menuCategoryNumber: Int, val menuCategoryName: String, val recipeNumber: Int, val recipeName: String, val recipePrintAsName: String, val ingredientList: String, val allergens: String, val recipePrintAsColor: String, val recipePrintAsCharacter: String, val recipeProductInformation: String, val sellingPrice: String, val portionCost: String, val productionDepartment: String, val serviceDepartment: String, val cateringDepartment: String, val recipeWebCodes: String, val serviceSize: String, val calories: String, val caloriesFromFat: String, val totalFat: String, val totalFatDv: String, val satFat: String, val satFatDv: String, val transFat: String, val transFatDv: String, val cholesterol: String, val cholesterolDv: String, val sodium: String, val sodiumDv: String, val totalCarb: String, val totalCarbDv: String, val dietaryFiber: String, val dietaryFiberDv: String, val sugars: String, val sugarsDv: String, val protein: String, val proteinDv: String): Parcelable
