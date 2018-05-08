分享一些自己做的小控件


1. 展开的加号按钮

![样式](OpenButton.gif) 

```kotlin
fun demo() {
    open_button.setNum(0) //设置默认数字
                .setTextSizeDp(30f) //设置字体大小
                .setAddImageRes(R.drawable.cart_add) //设置加号图标
                .setSubImageRes(R.drawable.cart_sub) //设置减号图标
                .addNumChangeListener { num, isAdd -> //添加数字改变监听
                    Toast.makeText(this, "num: $num, isAdd: $isAdd", Toast.LENGTH_SHORT).show()
                }
                
    open_button.getNum() //获取数量
}
```

2. 展开折叠的加入购物车按钮

![样式](AddToCartButton.gif) 

```kotlin
fun demo() {
    add_to_cart_button.setPromptBgColor(Color.parseColor("#26be8e")) //设置提示文字背景
                    .setPrompt("加入购物车") //设置提示文字
                    .setNumTextSizeDp(30f) //设置数字字体大小
                    .setPromptTextSizeDp(20f) //设置提示文字大小
                    .setPromptTextColor(Color.WHITE) //设置提示文字颜色
                    .setAddImageRes(R.drawable.cart_add) //设置加号图标
                    .setSubImageRes(R.drawable.cart_sub) //设置减号图标
                    .addNumChangeListener { num, isAdd -> //添加数字改变监听
                        Toast.makeText(this, "num: $num, isAdd: $isAdd", Toast.LENGTH_SHORT).show()
                    }
                    
    add_to_cart_button.getNum() //获取数量
}
```

3. 靠右侧的展开折叠加减按钮

![样式](RightOpenButton.gif) 

```kotlin
fun demo() {
    right_open_button.setNum(0) //设置默认数字
                    .setTextSizeDp(30f) //设置字体大小
                    .setAddImageRes(R.drawable.cart_add) //设置加号图标
                    .setSubImageRes(R.drawable.cart_sub) //设置减号图标
                    .addNumChangeListener { num, isAdd -> //添加数字改变监听
                        Toast.makeText(this, "num: $num, isAdd: $isAdd", Toast.LENGTH_SHORT).show()
                    }
                    
    right_open_button.getNum() //获取数量
}
```