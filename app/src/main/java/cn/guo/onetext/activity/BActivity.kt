package cn.guo.onetext.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

open class BActivity<V: ViewBinding>: AppCompatActivity(){

    lateinit var binding:V

    fun initBinding(b:V){
        binding = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}