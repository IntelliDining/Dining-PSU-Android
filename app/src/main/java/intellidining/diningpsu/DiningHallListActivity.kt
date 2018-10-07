package intellidining.diningpsu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_dining_hall_list.*
import kotlinx.android.synthetic.main.content_dining_hall_list.*
import kotlinx.android.synthetic.main.item_dining_hall.view.*


class DiningHallListActivity : AppCompatActivity() {
    companion object {

        val DINING_HALL_DIFFER = object : DiffUtil.ItemCallback<DiningHall>() {
            override fun areItemsTheSame(oldItem: DiningHall, newItem: DiningHall): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun areContentsTheSame(oldItem: DiningHall, newItem: DiningHall): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    class DiningHallViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val card: CardView = root.card_dining_hall
        val name: TextView = root.text_name
        val image: ImageView = root.image_dining_hall

    }

    class DiningHallAdapter : ListAdapter<DiningHall, DiningHallViewHolder>(DINING_HALL_DIFFER) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiningHallViewHolder {
            return DiningHallViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dining_hall, parent, false))
        }

        override fun onBindViewHolder(holder: DiningHallViewHolder, position: Int) {
            val item = getItem(position)

            holder.name.text = item.name

            if (item.imageIcon != null) {
                Glide.with(holder.itemView)
                        .load(item.imageIcon)
                        .apply(RequestOptions.centerCropTransform())
                        .into(holder.image)
            }

            holder.card.setOnClickListener {
                val context = holder.itemView.context

                val intent = Intent(context, DiningHallDetailActivity::class.java)

                intent.putExtra("dining_hall", item)

//                val options = ActivityOptionsCompat
//                        .makeSceneTransitionAnimation(context as Activity, holder.image, "hall_image")

//                context.startActivity(intent, options.toBundle())
                context.startActivity(intent)

            }


        }

        override fun onViewRecycled(holder: DiningHallViewHolder) {
            Glide.with(holder.itemView).clear(holder.image)
            holder.card.setOnClickListener(null)
        }
    }

    private val adapter = DiningHallAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dining_hall_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        list_dining_hall.layoutManager = GridLayoutManager(this, 2)
        list_dining_hall.adapter = adapter

        // val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        // list_dining_hall.addItemDecoration(SpacesItemDecoration(spacingInPixels))

//        adapter.submitList(listOf(
//                DiningHall("Findlay East", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_findlay_east")),
//                DiningHall("Mix at Pollock", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_mix_at_pollock")),
//                DiningHall("North", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_north")),
//                DiningHall("Pollock", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_pollock")),
//                DiningHall("Redifer South", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_redifer_south")),
//                DiningHall("West", Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_west"))
//        ))


        val diningHallImages = mapOf(
                "11" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_findlay_east"),
                "24" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_mix_at_pollock"),
                "17" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_north"),
                "14" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_pollock"),
                "13" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_redifer_south"),
                "16" to Uri.parse("android.resource://intellidining.diningpsu/drawable/hall_west")
        )


        API.getDiningHalls { diningHalls ->

            val hallsWithImages = diningHalls.map { it.copy(imageIcon = diningHallImages[it.number]) }

            adapter.submitList(hallsWithImages)

        }

    }

}
