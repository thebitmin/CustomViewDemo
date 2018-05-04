package tech.bitmin.customviewdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        open_button.setNum(0)
                .setTextSizeDp(30f)
                .setAddImageRes(R.drawable.cart_add)
                .setSubImageRes(R.drawable.cart_sub)
    }
}
