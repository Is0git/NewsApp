package com.is0git.cosmoplanetview.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.transform
import com.is0git.cosmoplanetview.R

class CosmoPlanetView : View {
    private lateinit var planetCirclePaint: Paint
    private lateinit var planetAtmospherePaint: Paint
    private var atmosphereColorId = R.color.defaultAtmosphereColor
    private var atmosphereColor: Int = 0
    private var atmosphereWidth = 0f
    private var atmosphereAmbianceRatio = 0.5f
    private var planetRadius = 150f
        set(value) {
            planetDiameter = value * 2
            field = value
        }
    private var planetDiameter = 0f
    private var atmosphereBlurRadius = 15f
    private var isShadowShown = true
    private lateinit var shadowPaint: Paint

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        var typedArray: TypedArray? = null
        if (attrs != null) {
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.CosmoPlanetView)
                typedArray.apply {
                    planetRadius = getDimension(R.styleable.CosmoPlanetView_planetRadius, 0f)
                    atmosphereColorId = getResourceId(
                        R.styleable.CosmoPlanetView_atmosphereColor,
                        R.color.defaultAtmosphereColor
                    )
                    atmosphereWidth = getDimension(R.styleable.CosmoPlanetView_atmosphereWidth, 0f)
                    atmosphereAmbianceRatio =
                        getFloat(R.styleable.CosmoPlanetView_planetAmbianceRatio, 0.5f)
                    isShadowShown = getBoolean(R.styleable.CosmoPlanetView_showShadow, true)
                }

            } catch (ex: Exception) {

            } finally {
                typedArray?.recycle()
            }
            atmosphereColor = ResourcesCompat.getColor(resources, atmosphereColorId, null)
            planetCirclePaint = Paint()
            planetAtmospherePaint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = atmosphereWidth
                strokeJoin = Paint.Join.BEVEL
                maskFilter = BlurMaskFilter(atmosphereBlurRadius, BlurMaskFilter.Blur.NORMAL)
            }
            shadowPaint = Paint()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val rectSmallerSide = widthSize.coerceAtMost(heightSize)
        planetRadius =
            if (rectSmallerSide / 2 >= planetRadius + atmosphereWidth) planetRadius else rectSmallerSide.toFloat() / 2f
        val totalHeightWidth =
            (planetRadius.toInt() * 2) + atmosphereWidth.toInt() + atmosphereBlurRadius.toInt()
        setMeasuredDimension(totalHeightWidth, totalHeightWidth)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // atmosphere shader
        val atmosphereColors = IntArray(3)
        atmosphereColors[0] = Color.WHITE
        atmosphereColors[1] = Color.parseColor("#4480bf")
        atmosphereColors[2] = Color.TRANSPARENT
        planetAtmospherePaint.shader = LinearGradient(
            w.toFloat(),
            0f,
            w.toFloat() * 0.5f,
            h.toFloat(),
            atmosphereColors,
            null,
            Shader.TileMode.CLAMP
        )
        // planet shader
        val map = BitmapFactory.decodeResource(resources, R.drawable.test2)
        val bitMapShader = BitmapShader(map, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        val planetAmbianceShader = LinearGradient(
            w.toFloat(),
            0f,
            w.toFloat() * atmosphereAmbianceRatio,
            h.toFloat() * atmosphereAmbianceRatio,
            Color.parseColor("#4480bf"),
            Color.BLACK,
            Shader.TileMode.CLAMP
        )
        val composeShader =
            ComposeShader(bitMapShader, planetAmbianceShader, PorterDuff.Mode.LIGHTEN)
        planetCirclePaint.shader = composeShader
        planetCirclePaint.shader.transform {
            this.setTranslate(300f, -600f)
            this.setScale(0.4f, 0.4f)
        }
        // shadow
        shadowPaint.shader = LinearGradient(
            width * 0.3f,
            height.toFloat() * 0.6f,
            width.toFloat(),
            0f,
            Color.BLACK,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            drawPlanet(canvas)
        }
    }

    private fun drawPlanet(canvas: Canvas) {
        val middle = width.toFloat() / 2f
        val path = Path()
        path.addCircle(middle, middle, planetRadius, Path.Direction.CW)
        canvas.drawPath(path, planetCirclePaint)
        canvas.drawPath(path, planetAtmospherePaint)
        if (isShadowShown) {
            canvas.drawPath(path, shadowPaint)
        }
    }
}