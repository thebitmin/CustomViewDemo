package tech.bitmin.customviewdemo

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        open_button.setNum(0) //设置默认数字
                .setTextSizeDp(30f) //设置字体大小
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标

        add_to_cart_button.setPromptBgColor(Color.parseColor("#26be8e")) //设置提示文字背景
                .setPrompt("加入购物车") //设置提示文字
                .setNumTextSizeDp(30f) //设置数字字体大小
                .setPromptTextSizeDp(20f) //设置提示文字大小
                .setPromptTextColor(Color.WHITE) //设置提示文字颜色
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标

        right_open_button.setNum(0) //设置默认数字
                .setTextSizeDp(30f) //设置字体大小
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标
    }
}
