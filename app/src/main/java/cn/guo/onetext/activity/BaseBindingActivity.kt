package cn.guo.onetext.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.viewbinding.ViewBinding

open class BaseBindingActivity<V:ViewBinding>:BaseActivity() {

    val TAG = javaClass.simpleName

    lateinit var binding:V

    fun initBinding(viewBinding:V){
        binding = viewBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(this::binding.isInitialized)
            setContentView(binding.root)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}