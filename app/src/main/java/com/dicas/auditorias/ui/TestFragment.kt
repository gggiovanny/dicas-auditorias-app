package com.dicas.auditorias.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicas.auditorias.R
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment() {

    private lateinit var textDisplay: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        textDisplay = arguments?.getString("text_display")!!

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        text_display.text = textDisplay
    }


}
