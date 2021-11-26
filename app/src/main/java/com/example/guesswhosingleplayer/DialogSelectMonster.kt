package com.example.guesswhosingleplayer

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.core.view.get
import androidx.core.view.size

//muestra el deck y se pide seleccionar un monstruo
class DialogSelectMonster(context: Context, private val player: Player, private  val itemType:Int): DialogFragment() {
    //interface de listener para la info que se recupera del dialog
    private lateinit var listener: DialogSelectMonsterListener
    interface DialogSelectMonsterListener {
        fun applySelectMonster(player: Player, itemType:Int)
    }

    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            var titulo:String="Selecciona tu monstruo"
            if(itemType==1){//Si se llama desde "tu monstruo es"
                titulo="Tu monstruo es..."
            }
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //vínculo con el layout selectmonsters_dialog.xml
            val binding = inflater.inflate(R.layout.selectmonsters_dialog, null)
            //Se crea el adaptador y se define el evento de click (para seleccionar al monstruo)
            val adaptador = AdaptadorMonsters(player,1){
                //Se asigna el evento click que selecciona una imagen
                onItemSelect(it, binding)
            }
            val recView_monstersSelect = binding.findViewById<RecyclerView>(R.id.recView_monstersSelect)
            recView_monstersSelect.layoutManager= GridLayoutManager(context, 10)
            recView_monstersSelect.adapter = adaptador
            //Botones del dialog
            builder.setView(binding)
                .setTitle(titulo)
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.applySelectMonster(player,itemType)
                    })
                .setNegativeButton("Cerrar",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.dismiss()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
    //Al iniciar el dialog se deshabilita el botón 'ok'
    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.visibility=View.INVISIBLE
            if(itemType==0){
                val negativeButton: Button = d.getButton(Dialog.BUTTON_NEGATIVE) as Button
                negativeButton.visibility=View.INVISIBLE
            }
        }
    }
    //Para hacer más grande el dialog
    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ActionBar.LayoutParams.MATCH_PARENT
        params.height = ActionBar.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }
    //para funcionamiento del listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as DialogSelectMonsterListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implementDialogSelectMonsterListener"))
        }
    }

    fun onItemSelect(it: Int, binding: View){
        val items: RecyclerView=binding.findViewById(R.id.recView_monstersSelect)
        if( items[it].background==null){
            //deselecciona el deck entero
            for (i in 0 until items.size) {
                items[i].background = null
                if(itemType==0){
                    player.setCardChoiced(-1)
                }
                else{
                    player.setCardChoicedAnswer(-1)
                }
            }
            //selecciona al monstruo al que se le hizo click
            items[it].setBackgroundResource(R.drawable.seleccionado)
            if(itemType==0){
                player.setCardChoiced(player.getMyDeck()[it].id)
            }
            else{
                player.setCardChoicedAnswer(player.getMyDeck()[it].id)
            }

        }//quita la selección si se vuelve a hacer click
        else{
            items[it].background = null
            if(itemType==0){
                player.setCardChoiced(-1)
            }
            else{
                player.setCardChoicedAnswer(-1)
            }

        }
        //Sí hay una elección se habilita el botón 'ok'
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            if(itemType==0){
                if(player.getCardChoiced()==-1){
                    positiveButton.visibility=View.INVISIBLE
                }else{positiveButton.visibility=View.VISIBLE}
            }else if(itemType==1){
                if(player.getCardChoicedAnswer()==-1){
                    positiveButton.visibility=View.INVISIBLE
                }else{positiveButton.visibility=View.VISIBLE}
            }

        }
    }
}