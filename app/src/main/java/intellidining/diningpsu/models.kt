package intellidining.diningpsu

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiningHall(val name: String, val imageIcon: Uri? = null) : Parcelable

@Parcelize
data class MenuItem(val name: String) : Parcelable

