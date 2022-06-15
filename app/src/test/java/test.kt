import android.content.Context
import com.sosacy.projetcoddity.data.model.Garbage
import android.database.DataSetObserver
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import java.util.ArrayList

internal class GarbageAdapter(var context: Context, var arrayList: ArrayList<Garbage>) :
    ListAdapter {
    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {}
    override fun unregisterDataSetObserver(observer: DataSetObserver) {}
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val subjectData = arrayList[position]
        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            convertView = layoutInflater.inflate(R.layout.list_row, null)
            convertView.setOnClickListener { }
            val tittle = convertView.findViewById<TextView>(R.id.title)
            val imag = convertView.findViewById<ImageView>(R.id.list_image)
            tittle.setText(subjectData.SubjectName)
            Picasso.with(context)
                .load(subjectData.Image)
                .into(imag)
        }
        return convertView
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return arrayList.size
    }

    override fun isEmpty(): Boolean {
        return false
    }
}