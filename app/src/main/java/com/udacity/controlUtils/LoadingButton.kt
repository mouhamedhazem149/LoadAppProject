package com.udacity.controlUtils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.math.log
import kotlin.math.min
import kotlin.properties.Delegates

const val ANIMATION_DURATION = 20000L

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var baseColor = 0
    private var loadingColor = 0
    private var textColor = 0
    private var circleColor = 0

    private var baseText = ""
    private var  loadingText = ""
    private var text = ""

    private var fontSize = 14f
    private var fontFamily = ""

    private var widthSize = 0
    private var heightSize = 0

    private var radius = 0

    //text will take 80% of the width
    var textZoneEnd = 0

    // circle will take remaining 20% of width with circle center at about 90% of width to make it central?
    var circleCenter = Point(0,0)

    private val valueAnimator = ValueAnimator.ofFloat(0f,100f)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    init {

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            baseColor = getColor(R.styleable.LoadingButton_baseColor, Color.GREEN)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor,
                resources.getColor(R.color.blueGray))

            textColor = getColor(R.styleable.LoadingButton_textColor,Color.WHITE)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, Color.YELLOW)

            baseText = getString(R.styleable.LoadingButton_baseText) ?: "Download"
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: "Loading"

            fontSize = getDimension(R.styleable.LoadingButton_textSize,55f)
            fontFamily = getString(R.styleable.LoadingButton_fontFamily) ?: "roboto"
        }
    }

    override fun performClick(): Boolean {
        startDownload()
        return super.performClick()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        layoutDirection = LAYOUT_DIRECTION_LTR
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    fun startDownload() {

        valueAnimator.duration = ANIMATION_DURATION

        valueAnimator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                buttonState = ButtonState.Loading
                isClickable = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                buttonState = ButtonState.Completed
                isClickable = true
                invalidate()
            }
        })

        valueAnimator.addUpdateListener {
            if (buttonState != ButtonState.Transition && it.isRunning && it.currentPlayTime >= 0.95 * it.duration) {
                val toAddTime = it.duration.toFloat() - it.currentPlayTime
                it.modulateAnimationTime(toAddTime)
            }
            invalidate()
        }
        valueAnimator.start()
    }

    fun endDownload() {
        buttonState = ButtonState.Transition

        val toAddTime = 50f
        if (valueAnimator.isRunning && valueAnimator.duration > toAddTime +  valueAnimator.currentPlayTime) {
            valueAnimator.modulateAnimationTime(toAddTime)
        }
    }

    private fun ValueAnimator.modulateAnimationTime(addedTime: Float) {
        pause()

        // ANIMATION_DURATION -> current
        // current + addedtime -> ?

        // basically the following try to retain the same "relative or % progress" while changin
        // the total duration taking benefit from the difference in absolute values which is reflected
        // in the change of the duration of the remaining part of the animation
        // 90 / 100 is the same as 9 / 10 but the absolute difference () the 2 numbers is different
        // trying to make new animation just started it from the beggining

        val current = currentPlayTime

        duration = current + addedTime.toLong()

        val calculated = current * (duration.toFloat() / ANIMATION_DURATION)

        currentPlayTime = calculated.toLong()

        resume()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // draw progress
        val progressWidth = (valueAnimator.animatedFraction % 1)* widthSize
        paint.color = loadingColor
        canvas?.drawRect(0f,0f,progressWidth,heightSize.toFloat(),paint)

        // draw baseRect
        paint.color = baseColor
        canvas?.drawRect(progressWidth,0f,widthSize.toFloat(),heightSize.toFloat(),paint)

        // drawText
        paint.color = textColor
        text = when (buttonState) {
            ButtonState.Loading -> loadingText
            ButtonState.Completed -> baseText
            ButtonState.Transition -> text
        }
        canvas?.drawText(text,widthSize / 2f,heightSize * 0.6f, paint)

        // drawCircle
        if (valueAnimator.animatedFraction % 1 != 0f) {
            // drawCircle
            paint.color = circleColor
            canvas?.drawArc(
                circleCenter.x.toFloat() - radius,
                circleCenter.y.toFloat() - radius,
                circleCenter.x.toFloat() + radius,
                circleCenter.y.toFloat() + radius,
                0f, (valueAnimator.animatedFraction % 1) * 360,
                true,
                paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth

        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)

        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        widthSize = w
        heightSize = h

        //text will take 80% of the width
        textZoneEnd = widthSize * 8 / 10

        // circle will take remaining 20% of width with circle center at about 90% of width to make it central?
        circleCenter.x = textZoneEnd / 2 + widthSize / 2
        circleCenter.y = heightSize / 2

        // zone for circle will be 20% with circle itselt about 14%
        radius = min(w,h) / 7

        setMeasuredDimension(w, h)
    }
}