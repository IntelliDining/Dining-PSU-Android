package intellidining.diningpsu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu_item_detail.*
import kotlinx.android.synthetic.main.content_menu_item_detail.*

class MenuItemDetailActivity : AppCompatActivity() {

    val String.coerce: String
        get() = if (isNotEmpty()) this else "?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuItem = intent.getParcelableExtra("menu_item") as? MenuItem

        if (menuItem == null) {
            finish()
            return
        }

        setContentView(R.layout.content_menu_item_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.title = menuItem.recipePrintAsName

        text_calories.text = menuItem.calories.coerce
        text_fat_calories.text = menuItem.caloriesFromFat.coerce
        text_total_fat.text = menuItem.totalFat
        text_saturated_fat.text = menuItem.satFat
        text_trans_fat.text = menuItem.transFat
        text_cholesterol.text = menuItem.cholesterol
        text_total_carbohydrates.text = menuItem.totalCarb
        text_dietary_fiber.text = menuItem.dietaryFiber
        text_sugars.text = menuItem.sugars
        text_protein.text = menuItem.protein

        text_ingredients.text = menuItem.ingredientList
        text_allergens.text = menuItem.allergens


    }
}
