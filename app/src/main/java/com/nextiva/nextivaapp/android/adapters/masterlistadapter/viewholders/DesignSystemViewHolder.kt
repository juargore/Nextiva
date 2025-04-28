package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DesignSystemListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemDesignSystemBinding

internal class DesignSystemViewHolder private constructor(itemView: View, var context: Context, masterListListener: MasterListListener) : BaseViewHolder<DesignSystemListItem>(itemView, context, masterListListener) {
    private val mMasterItemView: View

    lateinit var colorPatch: RelativeLayout
    lateinit var title: TextView
    lateinit var description: TextView

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_design_system, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        mMasterItemView = itemView
    }

    override fun bind(listItem: DesignSystemListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            title.setTextAppearance(R.style.DS_Body1)
        }
        title.text = listItem.title
        title.setTextColor(Color.DKGRAY)
        title.typeface = Typeface.DEFAULT
        title.isAllCaps = false

        if (listItem.resourceType == Enums.DesignSystemResourceType.SECTION) {
            colorPatch.visibility = View.GONE
            description.visibility = View.GONE
            title.setTextColor(Color.BLACK)
            title.typeface = Typeface.DEFAULT_BOLD
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                title.setTextAppearance(R.style.DS_Body2Heavy)
            }
        } else if (listItem.resourceType == Enums.DesignSystemResourceType.FONT) {
            colorPatch.visibility = View.GONE
            description.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                title.setTextAppearance(listItem.resourceId)
                val size = (title.textSize / context.getResources().getDisplayMetrics().scaledDensity).toInt()

                val typeface = title.typeface
                var typefaceString = ""
                if (typeface.isBold) { typefaceString += "bold " }
                if (typeface.isItalic) { typefaceString += "italic" }
                if (!typeface.isBold && !typeface.isItalic) { typefaceString = "regular" }

                val color = title.currentTextColor
                description.text = "size: $size\ncolor: ${getHex(color.toLong())}\nstyle: $typefaceString"
            }
        } else {
            colorPatch.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            val color = context.resources.getColor(listItem.resourceId)
            colorPatch.setBackgroundColor(color)
            description.text = "${getHex(color.toLong())}\n${getRGB(color.toLong())}"
        }
    }

    private fun getHex(color: Long): String {
        var hexColor = String.format("#%06X", 0x00FFFFFFL and color)
        if ((0xFF000000 and color.toLong()) != 0xFF000000L) {
            val opacityHex = String.format("#%02X",(0xFF000000 and color) shr 24)
            hexColor = "$opacityHex:$hexColor"
        }
        return hexColor
    }

    private fun getRGB(color: Long): String {
        val r = color and 0xFF0000 shr 16
        val g = color and 0xFF00 shr 8
        val b = color and 0xFF
        return "($r, $g, $b)"
    }

    private fun bindViews(view: View) {
        val binding = ListItemDesignSystemBinding.bind(view)
        colorPatch = binding.designSystemListItemColor
        title = binding.designSystemListItemTitle
        description = binding.fontAwesomeListItemDescription
    }
}