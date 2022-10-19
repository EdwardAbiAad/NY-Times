package com.example.nytimes.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nytimes.R
import com.example.nytimes.databinding.ActivityMainBinding
import com.example.nytimes.general.BaseRecyclerViewAdapter
import com.example.nytimes.model.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var adapter: BaseRecyclerViewAdapter<Result?>
    private var selectedPeriod = PeriodEnum.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViews()
        setViewModel()

    }

    private fun setViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {

                    is MainState.ShowLoadingState -> {
                        binding.pb.isVisible = true
                    }
                    is MainState.HideLoadingState -> {
                        binding.pb.isVisible = false
                    }
                    is MainState.ShowErrorMessage -> {
                        Toast.makeText(this@MainActivity, state.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                    is MainState.GetArticle -> {
                        state.result?.let { adapter.setItems(it) }
                    }
                }
            }
        }
    }

    private fun setViews() {

        updatePeriod()

        binding.listArticle.layoutManager = LinearLayoutManager(this)
        adapter = BaseRecyclerViewAdapter(R.layout.item_article, false) { position, data, view ->

            val txtTitle = view.findViewById<TextView>(R.id.txt_title)
            val txtDesc = view.findViewById<TextView>(R.id.txt_description)
            val txtSubPeriod = view.findViewById<TextView>(R.id.txt_subsection)
            val txtDate = view.findViewById<TextView>(R.id.txt_date)
            val llDescription = view.findViewById<LinearLayout>(R.id.ll_description)
            val listImages = view.findViewById<RecyclerView>(R.id.list_photos)

            val photoAdapter: BaseRecyclerViewAdapter<String?> =
                BaseRecyclerViewAdapter(R.layout.item_photos, true) { position, data, view ->

                    val img = view.findViewById<ImageView>(R.id.img)
                    data?.let {
                        Glide.with(view.context).load(it).into(img)
                    }

                }

            listImages.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            listImages.adapter = photoAdapter

            data?.media?.let { media ->
                val metadata = media.map { it?.mediaMetadata }
                val imagesList = arrayListOf<String?>()
                metadata.filter { it?.isNotEmpty() == true }.forEach { metadataList ->
                    metadataList?.map { metaData -> metaData?.url }?.let { imagesList.addAll(it) }
                }
                photoAdapter.setItems(imagesList as List<String?>)
            }

            txtTitle.text = data?.title ?: ""
            txtDesc.text = data?.abstract ?: ""
            txtSubPeriod.text = data?.subsection ?: ""
            txtDate.text = data?.publishedDate ?: ""

            view.setOnClickListener {
                llDescription.isVisible = !llDescription.isVisible
            }

        }
        binding.listArticle.adapter = adapter

        binding.btnPeriod1.setOnClickListener {
            selectedPeriod = PeriodEnum.PERIOD_1
            updatePeriod()
        }
        binding.btnPeriod7.setOnClickListener {
            selectedPeriod = PeriodEnum.PERIOD_7
            updatePeriod()
        }
        binding.btnPeriod30.setOnClickListener {
            selectedPeriod = PeriodEnum.PERIOD_30
            updatePeriod()
        }
    }

    private fun updatePeriod() {
        binding.txtSelected.text = when (selectedPeriod) {
            PeriodEnum.PERIOD_1 -> PeriodEnum.PERIOD_1.label
            PeriodEnum.PERIOD_7 -> PeriodEnum.PERIOD_7.label
            PeriodEnum.PERIOD_30 -> PeriodEnum.PERIOD_30.label
            PeriodEnum.NONE -> PeriodEnum.NONE.label
        }
        if (selectedPeriod != PeriodEnum.NONE) viewModel.getMostViewedArticles(selectedPeriod.number)
    }

    enum class PeriodEnum(val label: String, val number: Int) {
        PERIOD_1("Period 1 is Selected", 1),
        PERIOD_7("Period 7 is Selected", 7),
        PERIOD_30("Period 30 is Selected", 30),
        NONE("Please Select Period", 0)
    }
}