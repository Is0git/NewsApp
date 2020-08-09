package com.is0git.cosmoplanetview.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.transform
import com.is0git.cosmoplanetview.R
import com.is0git.cosmoplanetview.ui.animation_manager.AnimationManager
import com.is0git.cosmoplanetview.ui.animation_manager.animations.EnterCosmoAnimation
import com.is0git.cosmoplanetview.ui.animation_manager.animations.SpinCosmoAnimation

const val COSMO_PLANET_VIEW_TAG = "CosmoPlanetViewTag"

class CosmoPlanetView : View {
    private lateinit var animationManager: AnimationManager
    private lateinit var planetCirclePaint: Paint
    private lateinit var planetAtmospherePaint: Paint
    private lateinit var planetAmbiancePaint: Paint
    private lateinit var planetSkinPaint: Paint
    private lateinit var sunReflectionPaint: Paint
    private lateinit var planetBitmap: Bitmap
    private lateinit var shadowPaint: Paint
    private var atmosphereColorId = R.color.defaultAtmosphereColor

    @ColorRes
    private var atmosphereColor: Int = 0
    private var ambianceColor: Int = R.color.default_ambiance_color
    private var planetSkinDrawableId: Int = R.drawable.earth
    private var atmosphereWidth = 0f
    private var planetScaleX = 0.4f
    private var planetScaleY = 0.4f
    private var atmosphereAmbianceRatio = 0.5f
    var planetRadius = 150f
        set(value) {
            planetDiameter = value * 2
            field = value
        }
    private var planetDiameter = 0f
    private var atmosphereBlurRadius = 30f
    var isShadowShown = true
    private var atmosphereColors = IntArray(3).apply {
        this[0] = Color.parseColor("#a7ddf5")
        this[1] = Color.parseColor("#4480bf")
        this[2] = Color.TRANSPARENT
    }
    private var atmosphereRotation = 0.35f
        set(value) {
            field = value
            invalidate()
        }
    var skinHeight: Int = 0
    var skinWidth: Int = 0
    private var sunReflectionColors = IntArray(3).apply {
        this[0] = Color.parseColor("#b9e9ff")
        this[1] = Color.parseColor("#4480bf")
        this[2] = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    }
    var currentSpinX = 0f
    var currentSpinY = 0f
    private var sunReflectionWidth: Float = 8f

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
                    ambianceColor = getInt(
                        R.styleable.CosmoPlanetView_ambianceColor, R.color.default_ambiance_color
                    )
                    sunReflectionWidth =
                        getDimension(R.styleable.CosmoPlanetView_sunReflectionWidth, 8f)
                    planetSkinDrawableId =
                        this.getResourceId(R.styleable.CosmoPlanetView_planetSkin, R.drawable.earth)
                }

            } catch (ex: Exception) {

            } finally {
                typedArray?.recycle()
            }
            atmosphereColor = ResourcesCompat.getColor(resources, atmosphereColorId, null)

            atmosphereColors[0] = atmosphereColor
            planetAmbiancePaint = Paint()
            planetCirclePaint = Paint()
            planetSkinPaint = Paint()
            sunReflectionPaint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = sunReflectionWidth
                maskFilter = BlurMaskFilter(sunReflectionWidth, BlurMaskFilter.Blur.NORMAL)
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
        Log.d(COSMO_PLANET_VIEW_TAG, "ONMEAS")
        // if doesn't feet in screen, takes largest possible size in rectangle
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
        Log.d(COSMO_PLANET_VIEW_TAG, "SizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        // atmosphere shader
        // planet shader
        planetAtmospherePaint.shader =
            createAtmosphereGradient(w, h, atmosphereRotation, atmosphereColors)
        setPlanetSkin(planetSkinPaint, planetSkinDrawableId)
        setAmbianceShader(ambianceColor, planetAmbiancePaint, w.toFloat(), h.toFloat())
        sunReflectionPaint.shader =
            createAtmosphereGradient(w, h, atmosphereRotation, sunReflectionColors)
        setSpin(-0.6f, 0.8f)
        // shadow
        shadowPaint.shader = createShadowShader()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            drawPlanet(canvas)
        }
    }

    private fun createShadowShader(): Shader {
        return LinearGradient(
            width * 0.3f,
            height.toFloat() * 0.6f,
            width.toFloat() * 0.8f,
            height - (height * 0.8f),
            ResourcesCompat.getColor(resources, R.color.colorBackground, null),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
    }

    private fun setAmbianceShader(@ColorInt color: Int, paint: Paint, w: Float, h: Float) {
        paint.shader = LinearGradient(
            w,
            0f,
            w * atmosphereAmbianceRatio,
            h * atmosphereAmbianceRatio,
            color,
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
            4
        )
        val matrixBitmap = Bitmap.createBitmap(map, 0, 0, map.width, map.height, matrix, true)
        planetBitmap = matrixBitmap
        val bitMapShader = BitmapShader(map, Shader.TileMode.REPEAT, Shader.TileMode.MIRROR)
        paint.shader = bitMapShader
        skinHeight = map.height
        skinWidth = map.width
    }

    private fun createAtmosphereGradient(
        w: Int,
        h: Int,
        multiplier: Float,
        colors: IntArray
    ): LinearGradient {
        return LinearGradient(
            w.toFloat(),
            h.toFloat() * multiplier,
            w.toFloat() * 0.5f,
            h.toFloat() * 0.5f,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun drawPlanet(canvas: Canvas) {
        val middle = width.toFloat() / 2f
        val path = Path()
        path.addCircle(middle, middle, planetRadius - 50, Path.Direction.CW)
        canvas.drawPath(path, planetSkinPaint)
        canvas.drawPath(path, planetAmbiancePaint)
        canvas.drawCircle(middle, middle, planetRadius - 60, planetAtmospherePaint)
        canvas.drawCircle(middle, middle, planetRadius - 50, sunReflectionPaint)
        if (isShadowShown) {
            canvas.drawPath(path, shadowPaint)
        }
    }

    fun setSpin(spinX: Float = currentSpinX, spinY: Float = currentSpinY) {
        currentSpinX = spinX
        currentSpinY = spinY
        planetSkinPaint.shader.transform {
            this.setScale(planetScaleX, planetScaleY)
            postTranslate(spinX, spinY)
        }
    }

    fun setAtmosphereColor(color: Int) {
        atmosphereColors[0] = color
        atmosphereColors[1] = color
        planetAtmospherePaint.shader =
            createAtmosphereGradient(width, height, atmosphereRotation, atmosphereColors)
    }

    fun setAtmosphereRotationAnimated(atmosphereRotation: Float) {
        this.atmosphereRotation = atmosphereRotation
        planetAtmospherePaint.shader =
            createAtmosphereGradient(width, height, atmosphereRotation, atmosphereColors)
    }

    fun setSunReflection(color: Int) {
        sunReflectionColors[0] = color
        sunReflectionPaint.shader =
            createAtmosphereGradient(width, height, atmosphereRotation, sunReflectionColors)
    }

    fun setAmbianceColor(@ColorInt color: Int) {
        ambianceColor = color
        setAmbianceShader(color, planetAmbiancePaint, width.toFloat(), height.toFloat())
    }

    fun setSkin(@IdRes skinId: Int) {
        planetSkinDrawableId = skinId
        setPlanetSkin(planetSkinPaint, skinId)
        setSpin(currentSpinX, currentSpinX)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        post {
            animationManager = AnimationManager(this)
        }
    }

    fun playSpinAnimation() {
        updateSpin()
        animationManager.playCosmoAnimation(animationManager.spinAnimator)
    }

    fun updateSpin() {
        (animationManager.spinAnimator as SpinCosmoAnimation).updateSpin(currentSpinX)
    }

    fun pauseSpinAnimation() {
        animationManager.pauseCosmoSpinAnimation(animationManager.spinAnimator)
    }

    fun resumeSpinAnimation() {
        animationManager.resumeCosmoAnimation(animationManager.spinAnimator)
    }

    fun cancelSpinAnimation() {
        animationManager.cancelCosmoAnimation(animationManager.spinAnimator)
    }

    fun playEnterAnimation() {
        animationManager.playCosmoAnimation(animationManager.enterAnimator)
    }

    fun setEnterAnimationOnEndListener(onEnd: (() -> Unit)?) {
        post { (animationManager.enterAnimator as EnterCosmoAnimation).onEnd = onEnd }
    }
}