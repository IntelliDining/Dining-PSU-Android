package intellidining.diningpsu

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_dining_hall_detail.*
import kotlinx.android.synthetic.main.content_dining_hall_detail.*
import kotlinx.android.synthetic.main.item_food_menu_section.view.*
import java.util.*
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.item_food_menu.view.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class DiningHallDetailActivity : AppCompatActivity() {

    companion object {

        val MENU_ELEMENT_DIFFER = object : DiffUtil.ItemCallback<MenuElement>() {
            override fun areItemsTheSame(oldItem: MenuElement, newItem: MenuElement): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MenuElement, newItem: MenuElement): Boolean {
                return oldItem == newItem
            }

        }
    }

    sealed class MenuElement {
        companion object {
            const val TYPE_HEADER = 1
            const val TYPE_ITEM = 2
        }

        data class Header(val name: String) : MenuElement()
        data class Item(val item: MenuItem) : MenuElement()
    }

    class MenuItemHeaderViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val section: TextView = root.text_section

    }

    class MenuItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val container: ConstraintLayout = root.item_container
        val name: TextView = root.text_name
        val caption: TextView = root.text_caption

    }

    class MenuItemAdapter : ListAdapter<MenuElement, RecyclerView.ViewHolder>(MENU_ELEMENT_DIFFER) {

        override fun getItemViewType(position: Int): Int {
            return when (getItem(position)) {
                is MenuElement.Header -> MenuElement.TYPE_HEADER
                is MenuElement.Item -> MenuElement.TYPE_ITEM
                else -> throw IllegalArgumentException("Invalid viewType")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MenuElement.TYPE_HEADER ->
                    MenuItemHeaderViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_food_menu_section, parent, false))

                MenuElement.TYPE_ITEM ->
                    MenuItemViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_food_menu, parent, false))

                else -> throw IllegalArgumentException("Invalid viewType")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)

            when (item) {
                is MenuElement.Header -> {
                    holder as MenuItemHeaderViewHolder

                    holder.section.text = item.name


                }
                is MenuElement.Item -> {
                    holder as MenuItemViewHolder

                    holder.container.setOnClickListener {

                        val intent = Intent(holder.container.context, MenuItemDetailActivity::class.java)

                        intent.putExtra("menu_item", item.item)

                        holder.container.context.startActivity(intent)

                    }

                    holder.name.text = item.item.recipePrintAsName

                    fun coerce(x: String) = if (x.isNotEmpty()) x else "?"

                    holder.caption.text = "kcal: ${coerce(item.item.calories)}, fat: ${coerce(item.item.totalFat)}"

                }
            }

        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            when (holder) {
                is MenuItemViewHolder -> {
                    holder.container.setOnClickListener(null)
                }
            }
        }
    }

    private val adapter = MenuItemAdapter()

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
        list_item_menu.adapter = adapter

        list_item_menu.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val date = "10/05/2018"


//        adapter.submitList(listOf(
//                MenuElement.Header("Section 1"),
//                MenuElement.Item(MenuItem("Rice Krispies")),
//                MenuElement.Item(MenuItem("Mac & Cheese")),
//                MenuElement.Item(MenuItem("Salad")),
//                MenuElement.Header("Section 2"),
//                MenuElement.Item(MenuItem("Broccoli & Beef")),
//                MenuElement.Item(MenuItem("Rice")),
//                MenuElement.Item(MenuItem("Cookie")),
//                MenuElement.Header("Section 3"),
//                MenuElement.Item(MenuItem("Turkey"))
//        ))

        loadDay(diningHall, date)

        /* starts before 1 month from now */
        val startDate = Calendar.getInstance()

        /* ends after 1 month from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 5)

        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar, position: Int) {

                val formatter = SimpleDateFormat("MM/dd/yyyy")

                val dt = formatter.format(date.time)

                loadDay(diningHall, dt)

            }

        }

        fab.setOnClickListener { _ ->
            toggleCalendar()
        }
    }

    fun loadDay(diningHall: DiningHall, date: String) {

        API.getMenu(date, diningHall.number) { menu ->

            val collection = ArrayList<MenuElement>()

            for ((meal, map) in menu) {

                for ((location, items) in map) {

                    collection.add(MenuElement.Header("$meal / $location"))

                    for (item in items) {
                        collection.add(MenuElement.Item(item))
                    }
                }
            }

            adapter.submitList(collection)

        }

    }

    private fun toggleCalendar() {
        val normalHeight = resources.getDimensionPixelSize(R.dimen.app_bar_height)
        val expandedHeight = resources.getDimensionPixelSize(R.dimen.expanded_app_bar_height)


        val fromHeight: Int = app_bar.layoutParams.height

        val toHeight: Int = if (fromHeight < expandedHeight) expandedHeight else normalHeight

        calendarView.visibility = View.VISIBLE

        val params2 = calendarView.layoutParams
        params2.height = resources.getDimensionPixelSize(R.dimen.horizontal_calendar_height)
        calendarView.layoutParams = params2

        val animator = ValueAnimator.ofInt(fromHeight, toHeight)

        animator.addUpdateListener {

            val params = app_bar.layoutParams
            params.height = animator.animatedValue as Int
            app_bar.layoutParams = params

        }

        animator.duration = 300

        animator.start()

    }


}
