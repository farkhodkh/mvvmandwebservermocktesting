package com.stashinvest.stashchallenge.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import com.stashinvest.stashchallenge.R
import com.stashinvest.stashchallenge.api.model.ImageDetails

class PopUpDialogFragment : DialogFragment() {
    //region Privat fields
    lateinit var imageDetails: ImageDetails
    lateinit var txtVTitle: TextView
    lateinit var txtVArtist: TextView
    lateinit var imVMain: ImageView
    lateinit var similarsLinearLayout: LinearLayout
    //endregion

    //region Public methods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_image_details, container)
        txtVTitle = rootView.findViewById(R.id.txtVTitle)
        txtVArtist = rootView.findViewById(R.id.txtVArtist)
        imVMain = rootView.findViewById(R.id.imVMain)
        similarsLinearLayout = rootView.findViewById(R.id.similarsLinearLayout)

        if (!this::imageDetails.isInitialized){
            return rootView
        }

        val metaData = imageDetails.metadata.get(0)
        txtVTitle.text = metaData.title
        txtVArtist.text = metaData.artist

        Picasso.get()
            .load(imageDetails.uri)
            .into(imVMain)

        val imageViewLayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        for (image in imageDetails.similarImages) {
            val imageView = ImageView(requireContext()).apply {
                layoutParams = imageViewLayoutParams
                setPadding(8, 8, 8, 8)
            }

            Picasso.get()
                .load(image.thumbUri)
                .resize(300, 300)
                .into(imageView)

            similarsLinearLayout.addView(imageView)
        }

        return rootView
    }

    companion object {
        const val TAG = "PopUpDialogFragment"

        fun newInstance(imageDetails: ImageDetails) = PopUpDialogFragment().apply {
            this.imageDetails = imageDetails
        }
    }
    //endregion
}
