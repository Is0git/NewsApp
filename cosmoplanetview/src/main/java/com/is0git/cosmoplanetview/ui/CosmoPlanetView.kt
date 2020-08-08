package com.is0git.cosmoplanetview.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.transform
import com.is0git.cosmoplanetview.R

const val COSMO_PLANET_VIEW_TAG = "CosmoPlanetViewTag"

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
    private var atmosphereBlurRadius = 30f
    private var isShadowShown = true
    private lateinit var shadowPaint: Paint
    private var atmosphereColors = IntArray(3).apply {
        this[0] =  Color.parseColor("#a7ddf5")
        this[1] = Color.parseColor("#4480bf")
        this[2] = Color.TRANSPARENT
    }
    var atmosphereRotation = 0f
        set(value) {
            field = value
            invalidate()
        }
    var spinX = 0f
    var spinY = 0f
    private var skinHeight: Int = 0
    private var skinWidth: Int = 0
    private lateinit var composeShader: Shader
    private lateinit var planetAmbiancePaint: Paint
    private lateinit var planetSkinPaint: Paint
    private lateinit var sunReflectionPaint: Paint
    private lateinit var planetBitmap: Bitmap

    private var sunReflectionColors = IntArray(3).apply {
        this[0] =  Color.parseColor("#b9e9ff")
        this[1] = Color.parseColor("#4480bf")

        this[2] = Color.BLACK
    }

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
            planetAmbiancePaint = Paint()
            planetCirclePaint = Paint()
            planetSkinPaint = Paint()
            sunReflectionPaint = Paint().apply {
                strokeWidth = 8f
            }
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
        // planet shader
        planetAtmospherePaint.shader =
            setAtmosphereGradient(w, h, atmosphereRotation, atmosphereColors)
        setPlanetSkin(planetSkinPaint, R.drawable.yaya)
        setAmbianceShader(planetAmbiancePaint, w.toFloat(), h.toFloat())
//        composeShader =
//            ComposeShader(bitMapShader, planetAmbianceShader, PorterDuff.Mode.SCREEN)
//        planetCirclePaint.shader = composeShader
        sunReflectionPaint.shader = setAtmosphereGradient(w, h, atmosphereRotation, sunReflectionColors)
        sunReflectionPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 5f
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }
        setSpin(-0.6f, 0.8f)
        // shadow
        shadowPaint.shader = LinearGradient(
            width * 0.3f,
            height.toFloat() * 0.6f,
            width.toFloat() * 0.8f,
            height - (height * 0.8f),
            ResourcesCompat.getColor(resources, R.color.colorBackground, null),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            drawPlanet(canvas)
        }
    }

    private fun setAmbianceShader(paint: Paint, w: Float, h: Float) {
        paint.shader = LinearGradient(
            w,
            0f,
            w * atmosphereAmbianceRatio,
            h * atmosphereAmbianceRatio,
            Color.parseColor("#4480bf"),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
    }

    private fun setPlanetSkin(paint: Paint, @DrawableRes skinId: Int) {
        val map = BitmapFactory.decodeResource(resources, skinId)
        val mapWidthFloat = map.width.toFloat()
        val mapHeightFloat = map.height.toFloat()
        val matrix = Matrix()
        matrix.setPolyToPoly(
            floatArrayOf(
                0f, 0f,
                mapWidthFloat, 0f,
                mapWidthFloat, mapHeightFloat,
                0f, mapHeightFloat
            ), 0,
            floatArrayOf(
                0f, mapHeightFloat * 0f,
                mapWidthFloat * 0.2f, 0f,
                mapWidthFloat * 0.2f, mapHeightFloat * 0.2f,
                0f, mapHeightFloat * 0.2f
            ), 0,
            4)
        val matrixBitmap = Bitmap.createBitmap(map, 0, 0, map.width, map.height, matrix, true)
        planetBitmap = matrixBitmap
        val bitMapShader = BitmapShader(map, Shader.TileMode.REPEAT, Shader.TileMode.MIRROR)
        paint.shader = bitMapShader
        skinHeight = map.height
        skinWidth = map.width
    }

    private fun setAtmosphereGradient(
        w: Int,
        h: Int,
        multiplier: Float,
        colors: IntArray
    ): LinearGradient {
        return LinearGradient(
            w.toFloat(),
            h.toFloat() * 0.35f,
            w.toFloat() * 0.5f + (0.5f * multiplier),
            h.toFloat() * 0.5f - (1 * multiplier),
            colors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawPlanet(canvas: Canvas) {
        val middle = width.toFloat() / 2f
        val path = Path()
        path.addCircle(middle, middle, planetRadius - 50, Path.Direction.CW)
        if (atmosphereRotation > 0f) planetAtmospherePaint.shader =
            setAtmosphereGradient(width, height, atmosphereRotation, atmosphereColors)
        canvas.drawPath(path, planetSkinPaint)
        canvas.drawPath(path, planetAmbiancePaint)
        canvas.drawCircle(middle, middle, planetRadius - 60, planetAtmospherePaint)
        canvas.drawCircle(middle, middle, planetRadius - 50, sunReflectionPaint)
        if (isShadowShown) {
            canvas.drawPath(path, shadowPaint)
        }
    }

    fun setSpin(spinX: Float, spinY: Float) {
//        val matrix = Matrix()
//        matrix.
        planetSkinPaint.shader.transform {
            this.setScale(0.4f, 0.4f)
            postTranslate(spinX * skinWidth, 0f)

        }
    }

    fun setComposeSpin(spinX: Float, spinY: Float) {
//        val matrix = Matrix()
//        matrix.
        planetCirclePaint.shader.transform {
            this.setScale(0.6f, 0.6f)
            postTranslate(spinX * skinWidth, 0f)
        }
    }
}