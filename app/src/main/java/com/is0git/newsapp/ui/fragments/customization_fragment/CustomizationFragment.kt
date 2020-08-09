package com.is0git.newsapp.ui.fragments.customization_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.PlanetCustomizeFragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomizationFragment : Fragment(),
    View.OnTouchListener {
    lateinit var binding: PlanetCustomizeFragmentLayoutBinding
    lateinit var planetGestureDetector: ScaleGestureDetector
    var lastX = 0f
    var lastY = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlanetCustomizeFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSeekBars()
        setSwitches()
        setButtons()
        setSkins()
        handlePlanetTouchEvents()
    }

    private fun setSeekBars() {
        binding.planetAtmosphere.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val mProgress = progress / 100f
                binding.cosmoView.setAtmosphereRotationAnimated(mProgress)
                binding.cosmoView.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setSwitches() {
        binding.isShadowShownSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.cosmoView.isShadowShown = isChecked
            binding.cosmoView.invalidate()
        }
    }

    private fun setButtons() {
        binding.atmosphereColorButton.setOnClickListener {
            buildColorPickerDialog {
                binding.cosmoView.setAtmosphereColor(it)
            }
        }
        binding.sunReflectionColorButton.setOnClickListener {
            buildColorPickerDialog {
                binding.cosmoView.setSunReflection(it)
                binding.cosmoView.invalidate()
            }
        }
        binding.ambianceColorButton.setOnClickListener {
            buildColorPickerDialog {
                binding.cosmoView.setAmbianceColor(it)
            }
        }
    }

    private fun setSkins() {
        binding.earthImageView.setOnClickListener {
            binding.cosmoView.setSkin(R.drawable.earth)
            binding.cosmoView.invalidate()
        }
        binding.marsImageView.setOnClickListener {
            binding.cosmoView.setSkin(R.drawable.mars)
            binding.cosmoView.invalidate()
        }
        binding.earthTwoImageView.setOnClickListener {
            binding.cosmoView.setSkin(R.drawable.test2)
            binding.cosmoView.invalidate()
        }
    }

    private fun buildColorPickerDialog(onSelect: (color: Int) -> Unit) {
        MaterialColorPickerDialog.Builder(requireActivity())
            .setTitle("Pick color")
            .setColorListener { i, s ->
                onSelect(i)
                binding.cosmoView.invalidate()
            }
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handlePlanetTouchEvents() {
        binding.cosmoView.setOnTouchListener(this)
        planetGestureDetector =
            ScaleGestureDetector(
                requireContext(),
                object : ScaleGestureDetector.OnScaleGestureListener {

                    private val view: View? = null
                    private val gestureScale: ScaleGestureDetector? = null
                    private var scaleFactor = 1f
                    private var inScale = false

                    var oldScale = 0f

                    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                        inScale = true
                        return true
                    }

                    override fun onScaleEnd(detector: ScaleGestureDetector?) {

                    }

                    override fun onScale(detector: ScaleGestureDetector?): Boolean {
                        scaleFactor *= detector!!.scaleFactor
                        scaleFactor =
                            if (scaleFactor < 1) 1f else scaleFactor // prevent our view from becoming too small //

                        scaleFactor =
                            (scaleFactor * 100f) / 100f // Change precision to help with jitter when user just rests their fingers //

                        binding.cosmoView.scaleX = scaleFactor.coerceAtMost(1.4f)
                        binding.cosmoView.scaleY = scaleFactor.coerceAtMost(1.4f)
                        return true
                    }

                })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                planetGestureDetector.onTouchEvent(event)
                return true
            }
            ACTION_MOVE -> {
                binding.cosmoView.apply {
                    val mCurrentSpinX = currentSpinX + (event.rawX - lastX)
                    val mCurrentSpinY = currentSpinY + (event.rawY - lastY)
                    lastX = event.rawX
                    lastY = event.rawY
                    setSpin(mCurrentSpinX, mCurrentSpinY)
                    invalidate()
                }
                event.rawX
                planetGestureDetector.onTouchEvent(event)
                return true
            }
        }
        return planetGestureDetector.onTouchEvent(event)
    }
}