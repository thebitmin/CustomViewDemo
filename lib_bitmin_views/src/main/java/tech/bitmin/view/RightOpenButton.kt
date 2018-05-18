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
class RightOpenButton : View {

    private val paint = Paint()
    private var textPaint: TextPaint? = null
    private var addImageRes: Int? = null //加号图标
    private var subImageRes: Int? = null //减号图标
    private var addBitmap: Bitmap? = null
    private var subBitmap: Bitmap? = null
    private val addSrc = Rect() //默认加号减号图片大小相同
    private val addDst = Rect() //加号图片位置
    private var num: Long = 1 //显示数字
    private var maxNum = Long.MAX_VALUE //限制最大数量
    private var numStringWidth: Float = 0f //文字显示宽度
    private var numStringHeight: Float = 0f //文字显示高度
    private var rotate = 0f  //旋转角度，动画改变的角度
    private var translate = 0f //移动距离，动画改变的距离
    private var translateX = 0f //加号展开后偏移的距离，会在 onSizeChanged() 中赋值为布局高度
    private var openAnimatorSet: AnimatorSet? = null
    private var closeAnimatorSet: AnimatorSet? = null
    private var addOrSubRunnable: KeepAddOrSubRunnable? = null //自动加减的 Runnable
    private var point = Point()
    private var numChangeListenerList: ArrayList<(num: Long, isAdd: Boolean) -> Unit>? = null

    @Suppress("PrivatePropertyName")
    private val CLOSE = 0  //关闭状态
    @Suppress("PrivatePropertyName")
    private val OPENING = 1 //打开中
    @Suppress("PrivatePropertyName")
    private val OPEN = 2 //打开状态
    @Suppress("PrivatePropertyName")
    private val CLOSING = 3 //关闭中
    private var openStatus = CLOSE  //默认为关闭状态

    private var keepAddDelayed: Long = 16 //每次自动加减的间隔时间

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL

    }

    /**
     * 设置最大数量
     */
    @Suppress("unused")
    fun setMaxNum(maxNum: Long): RightOpenButton {
        if (maxNum < 1) {
            throw IllegalArgumentException("maxNum Can not less than 1")
        }
        this.maxNum = maxNum
        if (num > maxNum) {
            setNum(maxNum)
            numChangeListenerList?.forEach {
                it(num, true)
            }
        }
        return this
    }

    /**
     * 添加数字改变监听
     */
    @Suppress("unused")
    fun addNumChangeListener(listener: (num: Long, isAdd: Boolean) -> Unit): RightOpenButton {
        if (numChangeListenerList == null) {
            numChangeListenerList = ArrayList()
        }
        numChangeListenerList!!.add(listener)
        return this
    }

    /**
     * 删除所有监听
     */
    @Suppress("unused")
    fun removeAllListener(): RightOpenButton {
        numChangeListenerList?.clear()
        return this
    }

    /**
     * 获取布局高度
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        translateX = h.toFloat()
    }

    /**
     * 获取数量
     */
    @Suppress("unused")
    fun getNum(): Long {
        return num
    }

    /**
     * 设置字体大小
     */
    @Suppress("unused")
    fun setTextSizeDp(dp: Float): RightOpenButton {
        if (textPaint == null) {
            initTextPaint()
        }
        textPaint!!.textSize = dp2px(dp)
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
    fun setNum(num: Long): RightOpenButton {
        if (num < 0) {
            return this
        }
        if (num > maxNum) {
            return this
        }
        this.num = num
        if (num == 0L) {
            startCloseAnimator()
        }
        if (num > 0) {
            startOpenAnimator()
        }
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
     * 初始化文字笔
     */
    private fun initTextPaint() {
        textPaint = TextPaint()
        val paint = textPaint!!
        paint.textSize = dp2px(17f)
        paint.textAlign = Paint.Align.CENTER
        paint.flags = Paint.ANTI_ALIAS_FLAG
    }

    /**
     * 计算数字显示长度
     */
    private fun measureNumStringWidth() {
        if (textPaint == null) {
            initTextPaint()
        }
        numStringWidth = Layout.getDesiredWidth(num.toString(), textPaint!!)
    }

    /**
     * 计算字体高度
     */
    private fun measureNumStringHeight() {
        if (textPaint == null) {
            initTextPaint()
        }
        if (numStringHeight.toInt() != 0) {
            return
        }
        val metrics = textPaint!!.fontMetrics
        numStringHeight = metrics.descent - metrics.ascent
    }

    /**
     * 设置加号图标
     * 顺便更新加号 Bitmap 和 src
     */
    @Suppress("unused")
    fun setAddImageRes(res: Int): RightOpenButton {
        addImageRes = res
        addBitmap = BitmapFactory.decodeResource(resources, addImageRes!!)
        addSrc.set(0, 0, addBitmap!!.width, addBitmap!!.height)
        return this
    }


    /**
     * 设置减号图标
     * 但是不获取 bitmap，bitmap 到展开动画前获取，节省开支
     */
    @Suppress("unused")
    fun setSubImageRes(res: Int): RightOpenButton {
        subImageRes = res
        return this
    }

    /**
     * onDraw() 中画出所有图形
     * 默认状态只画加号
     * 调用顺序就图案叠加顺序
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
        drawSub(canvas)
        drawAdd(canvas)
    }

    /**
     * 画加号
     * bitmap.height = layout.height
     * add.gravity = right
     */
    private fun drawAdd(canvas: Canvas) {
        if (null == addImageRes) {
            return
        }
        if (addDst.bottom == 0) {
            initAddDst()
        }
        canvas.drawBitmap(addBitmap, addSrc, addDst, paint)
    }

    /**
     * 初始化图标位置
     */
    private fun initAddDst() {
        if (addBitmap == null) {
            throw NullPointerException("addBitmap == null")
        }
        val left = width - height
        val top = 0
        val right = width
        val bottom = height
        addDst.set(left, top, right, bottom)
    }

    /**
     * 画减号
     */
    private fun drawSub(canvas: Canvas) {
        if (openStatus == CLOSE) {
            return
        }
        if (subImageRes == null) {
            return
        }
        if (subBitmap == null) {
            subBitmap = BitmapFactory.decodeResource(resources, subImageRes!!)
        }
        if (addDst.bottom == 0) {
            initAddDst()
        }
        canvas.save()
        //移动画布，画布移动的距离与动画属性、文字宽度有关
        canvas.translate(-translate, 0f)
        //在移动文字距离
        canvas.translate(-numStringWidth, 0f)
        canvas.drawBitmap(subBitmap, addSrc, addDst, paint)
        canvas.restore()
    }

    /**
     * 画文字
     */
    private fun drawText(canvas: Canvas) {
        if (openStatus != OPEN) {
            return
        }
        canvas.save()
        canvas.translate(-translate / 2f, 0f)
        canvas.translate(-numStringWidth / 2f, 0f)
        canvas.drawText(num.toString(), width - height / 2f,
                height / 2f + numStringHeight / 3f, textPaint)
        canvas.restore()
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
                point.set(event.x.toInt(), event.y.toInt())
                add()
                sub()
                keepAdd()
                keepSub()
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
    private fun keepAdd() {
        //点击在图标之外不执行
        if (isCloseOutside()) {
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
    private fun keepSub() {
        //不在展开状态不执行
        if (openStatus != OPEN) {
            return
        }
        //点击在图标之外不执行
        if (isOpenSubOutside()) {
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
    private fun add() {
        //点击在图标之外不执行
        if (isCloseOutside()) {
            return
        }
        addOne()
    }

    /**
     * 数字加1
     */
    private fun addOne() {
        if (num == maxNum) {
            return
        }
        setNum(++num)
        numChangeListenerList?.forEach {
            it(num, true)
        }
    }

    /**
     * 减少数字
     */
    private fun sub() {
        if (openStatus != OPEN) {
            return
        }
        if (isOpenSubOutside()) {
            return
        }
        subOne()
    }

    /**
     * 数字减1
     */
    private fun subOne(): Boolean {
        if (num == 0L) {
            return false
        }
        setNum(--num)
        numChangeListenerList?.forEach {
            it(num, false)
        }
        return true
    }

    /**
     * 执行展开动画
     */
    private fun startOpenAnimator() {
        //已经展开不再执行
        if (openStatus != CLOSE) {
            return
        }
        //开启属性动画，改变属性
        if (openAnimatorSet == null) {
            initOpenAnimatorSet()
        }
        openAnimatorSet!!.start()
    }

    /**
     * 判断点击位置是否在图标之外
     */
    private fun isCloseOutside(): Boolean {
        return point.x < addDst.left
    }

    /**
     * 初始化展开动画
     */
    private fun initOpenAnimatorSet() {
        if (textPaint == null) {
            initTextPaint()
            measureNumStringWidth()
        }
        val rotateAnimator = ObjectAnimator.ofFloat(this, "rotate", 0f, 360f)
        val translateAnimator = ObjectAnimator.ofFloat(this, "translate", 0f, translateX * 2)
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
        openAnimatorSet!!.playTogether(rotateAnimator, translateAnimator)
    }

    /**
     * 执行按钮收回动画
     */
    private fun startCloseAnimator() {
        //数字不为 0 不执行
        if (num != 0L) {
            return
        }
        //已经收回不再执行
        if (openStatus == CLOSE) {
            return
        }
        //开启属性动画，改变属性
        if (closeAnimatorSet == null) {
            initCloseAnimatorSet()
        }
        closeAnimatorSet!!.start()
    }

    private fun isOpenSubOutside(): Boolean {
        val x = point.x - ((addDst.right + addDst.left) / 2f - translate - numStringWidth)
        val y = point.y - height / 2
        val distance = Math.sqrt((x * x + y * y).toDouble())
        val radio = height / 2
        return distance > radio
    }

    /**
     * 初始化收回动画
     */
    private fun initCloseAnimatorSet() {
        val rotateAnimator = ObjectAnimator.ofFloat(this, "rotate", 360f, 0f)
        val translateAnimator = ObjectAnimator.ofFloat(this, "translate", translateX * 2, 0f)
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
        closeAnimatorSet!!.playTogether(rotateAnimator, translateAnimator)
    }

    /**
     * 用于属性动画
     * 不执行 invalidate()
     * 因为和 setTranslate() 同时执行，只需要执行一次 invalidate()
     */
    @Suppress("unused")
    fun setRotate(rotate: Float) {
        this.rotate = rotate
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
            this@RightOpenButton.postDelayed(this, delayed)
        }
    }
}