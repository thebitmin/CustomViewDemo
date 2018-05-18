package tech.bitmin.customviewdemo

import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import tech.bitmin.view.LinearStartSnapHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        open_button.setNum(0) //设置默认数字
                .setTextSizeDp(30f) //设置字体大小
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标
                .removeAllListener() //删除所有监听
                .addNumChangeListener { num, isAdd -> //添加数字改变监听
                    Snackbar.make(root, "num: $num, isAdd: $isAdd", Snackbar.LENGTH_SHORT).show()
                }

        add_to_cart_button.setPromptBgColor(Color.parseColor("#26be8e")) //设置提示文字背景
                .setPrompt("加入购物车") //设置提示文字
                .setNumTextSizeDp(30f) //设置数字字体大小
                .setPromptTextSizeDp(20f) //设置提示文字大小
                .setPromptTextColor(Color.WHITE) //设置提示文字颜色
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标
                .removeAllListener() //删除所有监听
                .addNumChangeListener { num, isAdd -> //添加数字改变监听
                    Snackbar.make(root, "num: $num, isAdd: $isAdd", Snackbar.LENGTH_SHORT).show()
                }

        right_open_button.apply {
            setNum(0) //设置默认数量
            setMaxNum(100) //设置最大数量
            setTextSizeDp(30f) //设置字体大小
            setAddImageRes(R.drawable.cart_add) //设置加号图标
            setSubImageRes(R.drawable.cart_sub) //设置减号图标
            removeAllListener() //删除所有监听
            addNumChangeListener { num, isAdd -> //添加数字改变监听
                Snackbar.make(root, "num: $num, isAdd: $isAdd", Snackbar.LENGTH_SHORT).show()
            }
        }

        rcv_snap_start.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = RcvAdapter()
        rcv_snap_start.adapter = adapter
        val decoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        @Suppress("DEPRECATION")
        decoration.setDrawable(resources.getDrawable(R.drawable.shape_decoration))
        rcv_snap_start.addItemDecoration(decoration)
        LinearStartSnapHelper().attachToRecyclerView(rcv_snap_start)
    }

    private inner class RcvAdapter: RecyclerView.Adapter<RcvAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageView = holder.itemView as ImageView
            imageView.setImageResource(R.drawable.cart_add)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val imageView = ImageView(this@MainActivity)
            imageView.scaleType = ImageView.ScaleType.CENTER
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            imageView.layoutParams = params
            return ViewHolder(imageView)
        }

        override fun getItemCount(): Int {
            return 100
        }

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    }
}
