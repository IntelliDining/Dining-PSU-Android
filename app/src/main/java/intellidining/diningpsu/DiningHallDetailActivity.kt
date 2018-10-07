package intellidining.diningpsu

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_dining_hall_detail.*
import kotlinx.android.synthetic.main.content_dining_hall_detail.*
import kotlinx.android.synthetic.main.item_dining_hall.view.*
import kotlinx.android.synthetic.main.item_food_menu_section.view.*

class DiningHallDetailActivity : AppCompatActivity() {

    class MenuItemHeaderViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val section: TextView = root.text_section

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val diningHall = intent.getParcelableExtra("dining_hall") as? DiningHall

        if (diningHall == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_dining_hall_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.title = diningHall.name

        Glide.with(this)
                .load(diningHall.imageIcon)
                .apply(RequestOptions.centerCropTransform())
                .into(backdrop)


        list_item_menu.layoutManager = LinearLayoutManager(this)



        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }



}
