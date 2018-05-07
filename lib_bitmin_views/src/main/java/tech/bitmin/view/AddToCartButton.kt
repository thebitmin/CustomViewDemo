package tech.bitmin.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Bitmin on 2018/5/4.
 * Email: thebititmin@outlook.com
 * Blog: Bitmin.tech
 */
//@Suppress("unused", "PrivatePropertyName")
class AddToCartButton : View {

    private val paint = Paint()
    private var numPaint: TextPaint? = null
    private var promptPaint = TextPaint()
    private var prompt: String = "加入购物车"
    private var promptStringWidth = 0f //提示文字显示宽度
    private var promptStringHeight = 0f //提示文字显示高度
    private var promptBgPaint = Paint()
    private var addImageRes: Int? = null //加号图标
    private var subImageRes: Int? = null //减号图标
    private var addBitmap: Bitmap? = null
    private var subBitmap: Bitmap? = null
    private val promptBgLeftRectF = RectF()
    private val promptBgCenterRect = Rect()
    private val addSrc = Rect() //默认加号减号图片大小相同
    private val addDst = Rect() //默认加号减号图片大小相同
    private var num: Int = 0 //显示数字
    private var numStringWidth: Float = 0f //数字显示宽度
    private var numStringHeight: Float = 0f //数字显示高度
    private var translate = 0f //移动距离
    private var openAnimatorSet: AnimatorSet? = null
    private var closeAnimatorSet: AnimatorSet? = null
    private var addOrSubRunnable: KeepAddOrSubRunnable? = null
    private var translateX = 0f //加号展开后偏移的距离，会在 onSizeChanged() 中赋值为布局高度

    @Suppress("PrivatePropertyName")
    private val CLOSE = 0  //关闭状态
    @Suppress("PrivatePropertyName")
    private val OPENING = 1 //打开中
    @Suppress("PrivatePropertyName")
    private val OPEN = 2 //打开状态
    @Suppress("PrivatePropertyName")
    private val CLOSING = 3 //关闭中
    private var openStatus = CLOSE  //默认为关闭状态

    private var keepAddDelayed: Long = 16

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL

        promptBgPaint.flags = Paint.ANTI_ALIAS_FLAG
        promptBgPaint.style = Paint.Style.FILL

        initPromptPaint()
        measurePromptStringHeight()
        measureNumStringWidth()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        translateX = h.toFloat()
    }

    /**
     * 设置提示背景颜色
     */
    @Suppress("unused")
    fun setPromptBgColor(color: Int): AddToCartButton {
        promptBgPaint.color = color
        return this
    }

    /**
     * 设置提示
     * 例如加入购物车
     */
    @Suppress("unused")
    fun setPrompt(prompt: String): AddToCartButton {
        this.prompt = prompt
        measurePromptStringHeight()
        measurePromptStringWidth()
        return this
    }

    /**
     * 计算提示文字高度
     */
    private fun measurePromptStringHeight() {
        if (promptStringHeight.toInt() != 0) {
            return
        }
        val metrics = promptPaint.fontMetrics
        promptStringHeight = metrics.descent - metrics.ascent
    }

    /**
     * 计算提示文字宽度
     */
    private fun measurePromptStringWidth() {
        promptStringWidth = Layout.getDesiredWidth(prompt, promptPaint)
    }

    /**
     * 获取数量
     */
    @Suppress("unused")
    fun getNum(): Int {
        return num
    }

    /**
     * 设置提示字体大小
     */
    @Suppress("unused")
    fun setPromptTextSizeDp(dp: Float): AddToCartButton {
        promptPaint.textSize = dp2px(dp)
        return this
    }

    /**
     * 设置提示字体颜色
     */
    @Suppress("unused")
    fun setPromptTextColor(color: Int): AddToCartButton {
        promptPaint.color = color
        return this
    }

    /**
     * 设置数字字体大小
     */
    @Suppress("unused")
    fun setNumTextSizeDp(dp: Float): AddToCartButton {
        if (numPaint == null) {
            initNumPaint()
        }
        numPaint!!.textSize = dp2px(dp)
        //计算数字显示宽度
        measureNumStringWidth()
        //计算数字显示高度
        measureNumStringHeight()
        return this
    }

    /**
     * 设置显示的数字
     * 如果控件已经展开计算数字显示长用的宽度
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setNum(num: Int): AddToCartButton {
        this.num = num
        if (openStatus == CLOSE) {
            return this
        }
        //计算数字显示宽度
        measureNumStringWidth()
        //计算数字显示高度
        measureNumStringHeight()
        //重绘
        invalidate()
        return this
    }

    /**
     * 初始化提示文字笔
     */
    private fun initPromptPaint() {
        promptPaint = TextPaint()
        val paint = promptPaint
        paint.textSize = dp2px(17f)
        paint.textAlign = Paint.Align.CENTER
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = Color.WHITE
    }

    /**
     * 初始化数字笔
     */
    private fun initNumPaint() {
        numPaint = TextPaint()
        val paint = numPaint!!
        paint.textSize = dp2px(17f)
        paint.textAlign = Paint.Align.CENTER
        paint.flags = Paint.ANTI_ALIAS_FLAG
    }

    /**
     * 计算数字显示长度
     */
    private fun measureNumStringWidth() {
        if (numPaint == null) {
            initNumPaint()
        }
        numStringWidth = Layout.getDesiredWidth(num.toString(), numPaint!!)
    }

    /**
     * 计算字体高度
     */
    private fun measureNumStringHeight() {
        if (numPaint == null) {
            initNumPaint()
        }
        if (numStringHeight.toInt() != 0) {
            return
        }
        val metrics = numPaint!!.fontMetrics
        numStringHeight = metrics.descent - metrics.ascent
    }

    /**
     * 设置加号图标
     * 顺便更新加号 Bitmap 和 src
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun setAddImageRes(res: Int): AddToCartButton {
        addImageRes = res
        return this
    }

    /**
     * 设置减号图标
     * 但是不获取 bitmap，bitmap 到展开动画前获取，节省开支
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun setSubImageRes(res: Int): AddToCartButton {
        subImageRes = res
        return this
    }

    /**
     * 需要画
     * 1. 最上层加入购物车提示
     * 2. 下层加减按钮和数字
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawNumText(canvas)
        drawSub(canvas)
        drawAdd(canvas)
        drawPrompt(canvas)
    }

    private fun drawPrompt(canvas: Canvas) {
        //画左边半圆
        drawPromptBgLeft(canvas)
        //画中间矩形
        drawPromptBgCenter(canvas)
        //画右边半圆
        drawPromptBgRight(canvas)
        //画文字咯，码农太寂寞，注释上和自己聊起天
        if (openStatus != CLOSE) {
            return
        }
        canvas.drawText(prompt, width / 2f, height / 2f + promptStringHeight / 3f, promptPaint)
    }

    /**
     * 画提示文字左边半圆，自己测试的时候和矩形连接处有缝隙所以角度大了一些
     */
    private fun drawPromptBgLeft(canvas: Canvas) {
        if (openStatus == OPEN) {
            return
        }
        canvas.save()
        canvas.translate(-numStringWidth / 2 - translateX + translate, 0f)
        if (promptBgLeftRectF.bottom.toInt() == 0) {
            initPromptLeftRectF()
        }
        canvas.drawArc(promptBgLeftRectF, 80f, 200f, true, promptBgPaint)
        canvas.restore()
    }

    /**
     * 画提示文字中间矩形
     */
    private fun drawPromptBgCenter(canvas: Canvas) {
        if (openStatus == OPEN) {
            return
        }
        setPromptCenterRect() //每次绘画重新计算位置
        canvas.drawRect(promptBgCenterRect, promptBgPaint)
    }

    /**
     * 画提示文字右边半圆，自己测试的时候和矩形连接处有缝隙所以角度大了一些
     */
    private fun drawPromptBgRight(canvas: Canvas) {
        if (openStatus == OPEN) {
            return
        }
        canvas.save()
        canvas.translate(numStringWidth / 2 + translateX, 0f)
        if (promptBgLeftRectF.bottom.toInt() == 0) {
            initPromptLeftRectF()
        }
        canvas.drawArc(promptBgLeftRectF, -100f, 200f, true, promptBgPaint)
        canvas.restore()
    }

    /**
     * 左半圆在画布中的位置
     * 右边半圆也是这个位置了
     * 加号减号也是这个位置了
     */
    private fun initPromptLeftRectF() {
        val radio = height / 2f
        val left = width / 2f - radio
        val top = 0f
        val right = width / 2f + radio
        val bottom = height.toFloat()
        promptBgLeftRectF.set(left, top, right, bottom)
    }

    /**
     * 中间矩形在画布中的位置
     */
    private fun setPromptCenterRect() {
        val left = width / 2f - numStringWidth / 2 - translateX + translate
        val top = 0
        val right = width / 2f + numStringWidth / 2 + translateX
        val bottom = height
        promptBgCenterRect.set(left.toInt(), top, right.toInt(), bottom)
    }

    /**
     * 画减号
     */
    private fun drawSub(canvas: Canvas) {
        if (openStatus == CLOSE) {
            return
        }
        if (addDst.right == 0) {
            initAddDst()
        }
        if (subImageRes == null) {
            return
        }
        if (subBitmap == null) {
            subBitmap = BitmapFactory.decodeResource(resources, subImageRes!!)
        }
        if (addSrc.right == 0) {
            addSrc.set(0, 0, subBitmap!!.width, subBitmap!!.height)
        }
        canvas.save()
        //在展开状态时移动距离加上文字宽度
        //todo 这样并不优雅，最好能够通过动画展开，暂时有个思路，判断 numStringWidth 改变时开始动画。
        canvas.translate(-numStringWidth / 2 - translateX, 0f)
        canvas.drawBitmap(subBitmap, addSrc, addDst, paint)
        canvas.restore()
    }

    /**
     * 画加号
     * bitmap高度使用布局高度
     */
    private fun drawAdd(canvas: Canvas) {
        if (null == addImageRes) {
            return
        }
        if (addDst.right == 0) {
            initAddDst()
        }
        if (addBitmap == null) {
            addBitmap = BitmapFactory.decodeResource(resources, addImageRes!!)
        }
        if (addSrc.right == 0) {
            addSrc.set(0, 0, addBitmap!!.width, addBitmap!!.height)
        }
        canvas.save()
        //在展开状态时移动距离加上文字宽度
        //todo 这样并不优雅，最好能够通过动画展开，暂时有个思路，判断 numStringWidth 改变时开始动画。
        canvas.translate(numStringWidth / 2 + translateX, 0f)
        canvas.drawBitmap(addBitmap, addSrc, addDst, paint)
        canvas.restore()
    }

    /**
     * 初始化图标位置
     */
    private fun initAddDst() {
        val left = (width - height) / 2f
        val top = 0
        val right = left + height
        val bottom = height
        addDst.set(left.toInt(), top, right.toInt(), bottom)
    }

    /**
     * 画文字
     */
    private fun drawNumText(canvas: Canvas) {
        if (openStatus == CLOSE) {
            return
        }
        canvas.drawText(num.toString(), width / 2f,
                height / 2f + numStringHeight / 3f, numPaint)
    }

    /**
     * 控制手势操作
     *
     * 手势功能都分开成各个方法，虽然看起来舒服，但是逻辑有冗余
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                open(event)
                add(event)
                sub(event)
                close(event)
                keepAdd(event)
                keepSub(event)
            }
            MotionEvent.ACTION_UP -> {
                //取消自动加减
                removeAddOrSubRunnable()
            }
        }
        return true
    }

    /**
     * 取消自动加减
     */
    private fun removeAddOrSubRunnable() {
        if (addOrSubRunnable == null) {
            return
        }
        removeCallbacks(addOrSubRunnable)
    }

    /**
     * 持续加
     */
    private fun keepAdd(event: MotionEvent) {
        //不在展开状态不执行
        if (openStatus != OPEN) {
            return
        }
        //点击在图标之外不执行
        if (isOpenAddOutside(event)) {
            return
        }
        removeAddOrSubRunnable()
        if (addOrSubRunnable == null) {
            addOrSubRunnable = KeepAddOrSubRunnable(keepAddDelayed)
        }
        addOrSubRunnable!!.add()
        postDelayed(addOrSubRunnable, 500)
    }

    /**
     * 持续减
     */
    private fun keepSub(event: MotionEvent) {
        //不在展开状态不执行
        if (openStatus != OPEN) {
            return
        }
        //点击在图标之外不执行
        if (isOpenSubOutside(event)) {
            return
        }
        removeAddOrSubRunnable()
        if (addOrSubRunnable == null) {
            addOrSubRunnable = KeepAddOrSubRunnable(keepAddDelayed)
        }
        addOrSubRunnable!!.sub()
        postDelayed(addOrSubRunnable, 500)
    }

    /**
     * 增加数字
     */
    private fun add(event: MotionEvent) {
        //不在展开状态不执行
        if (openStatus != OPEN) {
            return
        }
        //点击在图标之外不执行
        if (isOpenAddOutside(event)) {
            return
        }
        addOne()
    }

    /**
     * 数字加1
     */
    private fun addOne() {
        setNum(++num)
    }

    /**
     * 展开后，是否点击加号外
     */
    private fun isOpenAddOutside(event: MotionEvent): Boolean {
        val x = event.x - (width / 2 + numStringWidth / 2 + translateX)
        val y = event.y - height / 2
        val distance = Math.sqrt((x * x + y * y).toDouble())
        val radio = height / 2
        return distance > radio
    }

    /**
     * 减少数字
     */
    private fun sub(event: MotionEvent) {
        if (openStatus != OPEN) {
            return
        }
        if (isOpenSubOutside(event)) {
            return
        }
        subOne()
    }

    /**
     * 数字减1
     */
    private fun subOne(): Boolean {
        if (num == 0) {
            return false
        }
        setNum(--num)
        return true
    }

    private fun isOpenSubOutside(event: MotionEvent): Boolean {
        val x = event.x - (width / 2 - numStringWidth / 2 - translateX)
        val y = event.y - height / 2
        val distance = Math.sqrt((x * x + y * y).toDouble())
        val radio = height / 2
        return distance > radio
    }

    /**
     * 执行按钮收回动画
     */
    private fun close(event: MotionEvent) {
        //数字不为 0 不执行
        if (num != 0) {
            return
        }
        //已经收回不再执行
        if (openStatus == CLOSE) {
            return
        }
        //点击在图标之外不执行
        if (isOpenSubOutside(event)) {
            return
        }
        //开启属性动画，改变属性
        if (closeAnimatorSet == null) {
            initCloseAnimatorSet()
        }
        closeAnimatorSet!!.start()
    }

    /**
     * 执行展开动画
     */
    private fun open(event: MotionEvent) {
        //已经展开不再执行
        if (openStatus != CLOSE) {
            return
        }
        //点击在图标之外，不执行
        if (isCloseOutside(event)) {
            return
        }
        //开启属性动画，改变属性
        if (openAnimatorSet == null) {
            initOpenAnimatorSet()
        }
        openAnimatorSet!!.start()
        //数字加1
        addOne()
    }

    /**
     * 判断点击位置是否在图标之外
     */
    private fun isCloseOutside(event: MotionEvent): Boolean {
        val radio = numStringWidth / 2 + translateX + height / 2f
        return event.x < (width / 2f - radio) || event.x > (width / 2f + radio)
    }

    /**
     * 初始化展开动画
     */
    private fun initOpenAnimatorSet() {
        if (numPaint == null) {
            initNumPaint()
            measureNumStringWidth()
        }
        val translateAnimator = ObjectAnimator.ofFloat(this, "translate", 0f, numStringWidth + translateX * 2)
        openAnimatorSet = AnimatorSet()
        openAnimatorSet!!.duration = 300
        openAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                openStatus = OPEN
            }

            override fun onAnimationStart(animation: Animator?) {
                openStatus = OPENING
            }
        })
        openAnimatorSet!!.playTogether(translateAnimator)
    }

    /**
     * 初始化收回动画
     */
    private fun initCloseAnimatorSet() {
        val translateAnimator = ObjectAnimator.ofFloat(this, "translate", numStringWidth + translateX * 2, 0f)
        closeAnimatorSet = AnimatorSet()
        closeAnimatorSet!!.duration = 300
        closeAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                openStatus = CLOSE
            }

            override fun onAnimationStart(animation: Animator?) {
                openStatus = CLOSING
            }
        })
        closeAnimatorSet!!.playTogether(translateAnimator)
    }

    /**
     * 用于属性动画
     */
    @Suppress("unused")
    fun setTranslate(translate: Float) {
        this.translate = translate
        invalidate()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun dp2px(dp: Float): Float {
        return (resources.displayMetrics.density * dp)
    }

    private inner class KeepAddOrSubRunnable(private var delayed: Long) : Runnable {

        private var isAdd = true


        fun add() {
            isAdd = true
        }

        fun sub() {
            isAdd = false
        }

        override fun run() {
            if (isAdd) {
                addOne()
            } else {
                if (!subOne()) {
                    return
                }
            }
            this@AddToCartButton.postDelayed(this, delayed)
        }
    }
}