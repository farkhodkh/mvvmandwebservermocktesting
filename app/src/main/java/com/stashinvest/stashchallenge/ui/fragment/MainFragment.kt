package com.stashinvest.stashchallenge.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.stashinvest.stashchallenge.App
import com.stashinvest.stashchallenge.R
import com.stashinvest.stashchallenge.api.model.ImageDetails
import com.stashinvest.stashchallenge.api.model.ImageResult
import com.stashinvest.stashchallenge.ui.adapter.ViewModelAdapter
import com.stashinvest.stashchallenge.ui.factory.ImageFactory
import com.stashinvest.stashchallenge.ui.viewmodel.MainFragmentViewModel
import com.stashinvest.stashchallenge.util.SpaceItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : Fragment() {
    //region Private fields
    private val compositeDisposable = CompositeDisposable()
    //endregion

    //region Public fields
    @Inject
    lateinit var imageFactory: ImageFactory

    @Inject
    lateinit var adapter: ViewModelAdapter

    @Inject
    lateinit var viewModel: MainFragmentViewModel

    private val space:
            Int by lazy { requireContext().resources.getDimensionPixelSize(R.dimen.image_space) }
    //endregion

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    //region Public methods
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupBindings()

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    fun setupBindings() {
        viewModel.getObserverState().observe(this, itemObserver)

        viewModel.isProgressBarVisibility
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.visibility = if (it) VISIBLE else GONE
            }
            .addTo(compositeDisposable)

        viewModel
            .imageMetadata
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageDetails ->
                showImageDetailsDialog(imageDetails)
            }, {
                showMessage(it.localizedMessage)
            })
            .addTo(compositeDisposable)
    }

    private val itemObserver = Observer<List<ImageResult>>{ images ->
        updateImages(images)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchPhrase.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                return@setOnEditorActionListener true
            }
            false
        }

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(space, space, space, space))
    }
    //endregion

    //region Private methods
    private fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun search() {
        viewModel.searchImages(searchPhrase.text.toString())
    }

    private fun updateImages(images: List<ImageResult>) {
        val viewModels = images.map {
            imageFactory.createImageViewModel(it, ::onImageLongPress)
        }
        adapter.setViewModels(viewModels)
    }

    private fun showImageDetailsDialog(imageDetails: ImageDetails) {
        fragmentManager?.let {
            PopUpDialogFragment
                .newInstance(imageDetails)
                .show(it, PopUpDialogFragment.TAG)
        }
    }

    private fun onImageLongPress(id: String, uri: String?) {
        viewModel.getImageMetadata(id, uri)
    }
    //endregion
}
