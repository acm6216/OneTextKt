package cn.guo.onetext.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.*
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import cn.guo.onetext.R

class BaseInputDialog(private val context: Context){

    private lateinit var builder:AlertDialog.Builder
    private lateinit var layout: View
    private lateinit var dialog:AlertDialog
    private lateinit var editText:EditText

    private fun build(){
        builder = AlertDialog.Builder(context)
        layout = LayoutInflater.from(context).inflate(R.layout.dialog_input,null,false)
        builder.setView(layout)
        findViews()
        setListener()
        dialog = builder.show()
        dialog.dismiss()
    }

    private fun findViews(){
        editText = layout.findViewById<EditText>(R.id.edit_query)
    }

    private fun setListener(){

    }

    init {
        build()
    }

    fun setTitle(title:String){
        dialog.setTitle(title)
    }

    fun setText(text:String){
        editText.setText(text)
    }

    fun setPositive(text:String,listener: OnClickListener){
        dialog.setButton(BUTTON_POSITIVE,text,listener)
        dialog.create()
    }
    fun setNegative(text:String,listener:OnClickListener){
        dialog.setButton(BUTTON_NEGATIVE,text,listener)
        dialog.create()
    }
    fun setNeutral(text:String,listener:OnClickListener){
        dialog.setButton(BUTTON_NEUTRAL,text,listener)
        dialog.create()
    }

    fun getText():String =editText.text.toString()

    fun show(){
        dialog.show()
    }

    fun dismiss(){
        dialog.dismiss()
    }
}