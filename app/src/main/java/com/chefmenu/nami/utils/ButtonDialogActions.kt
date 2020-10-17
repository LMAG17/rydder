package com.chefmenu.nami.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.chefmenu.nami.BuildConfig
import com.chefmenu.nami.Detail
import com.chefmenu.nami.R
import com.chefmenu.nami.controllers.services.ServiceFactory
import com.chefmenu.nami.models.detailModels.DetailResponse
import com.chefmenu.nami.models.sections.OrdersList
import com.chefmenu.nami.presenters.DetailPresenter
import com.chefmenu.nami.presenters.SectionPresenter
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.realm.RealmList
import kotlinx.android.synthetic.main.action_item.view.*
class ButtonDialogActions {
    fun actionsSection(
        mContext: Context,
        presenter: SectionPresenter,
        layoutActions: LinearLayout,
        dialog: BottomSheetDialog,
        items: OrdersList,
        actions: RealmList<Int>?,
        verDetalle: (OrdersList) -> Unit
    ) {
        for (id in actions!!) {
            val v: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            v.setOnClickListener {
                if (id == 2 || id == 4) {//Ver detalle
                    verDetalle(items)
                } else {
                    secondDialog(mContext, items, id, presenter)
                }
                dialog.dismiss()
            }

            v.action.text =
                ServiceFactory.data.actions!!.firstOrNull { it.id == id }?.name

            when (id) {
                4 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.save_icon,
                        0,
                        0,
                        0
                    )
                }
                5 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.release,
                        0,
                        0,
                        0
                    )
                }
                6 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.car,
                        0,
                        0,
                        0
                    )
                }
                7 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.persons,
                        0,
                        0,
                        0
                    )
                }
                8 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.freeze_icon,
                        0,
                        0,
                        0
                    )
                }
            }
            layoutActions.addView(v)
        }
    }

    private fun secondDialog(
        mContext: Context,
        items: OrdersList,
        id: Int,
        presenter: SectionPresenter
    ) {
        val activity = mContext as Activity
        activity.runOnUiThread(Runnable {
            val dialog = BottomSheetDialog(mContext)
            val dialogView =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.activity_popup, null)
            val title =
                dialogView.findViewById<TextView>(R.id.titleOrderId)
            val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
            val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
            val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
            val observationsView =
                dialogView.findViewById<EditText>(R.id.editObservations)
            val layoutActions =
                dialogView.findViewById<LinearLayout>(R.id.listActions)
            when (id) {
                5 -> {
                    title.text = "¿Esta seguro de liberar la orden #${items.id}?"
                    // observationsView.visibility = View.VISIBLE
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        var observations = observationsView.text.toString()
                        presenter.actionRelease(items.id!!, observations)
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
                6 -> {
                    title.text =
                        "¿Esta seguro de entregar la orden #${items.id} a un domiciliario?"
                    if (!BuildConfig.DEBUG) {
                        emailEdit!!.visibility = View.VISIBLE
                        phoneEdit!!.visibility = View.VISIBLE
                    }
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        if (!BuildConfig.DEBUG) {
                            presenter.actionPutDeliverCourier(
                                items.id!!,
                                emailEdit.text.toString(),
                                phoneEdit.text.toString()
                            )
                        } else {
                            presenter.actionPutDeliverCourier(
                                items.id!!
                            )
                        }
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
                7 -> {
                    title.text = "¿Esta seguro de entregar la orden #${items.id} al cliente?"
                    codeEdit!!.visibility = View.VISIBLE
                    if (!BuildConfig.DEBUG) {
                        emailEdit!!.visibility = View.VISIBLE
                        phoneEdit!!.visibility = View.VISIBLE

                    }
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        presenter.actionPutConfirmDelivery(
                            items.id!!,
                            codeEdit.text.toString(),
                            { secondDialog(mContext, items, 7, presenter) }
                        )
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)

                    if (!BuildConfig.DEBUG) {
                        val reSend: View =
                            LayoutInflater.from(mContext)
                                .inflate(R.layout.action_item, null)
                        reSend.setOnClickListener {
                            presenter.actionPutSendConfirmation(
                                items.id!!,
                                emailEdit!!.text.toString(),
                                phoneEdit!!.text.toString()
                            )
                            // dialog.dismiss()
                        }
                        reSend.action.text = "Reenviar codigo"
                        reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.message,
                            0,
                            0,
                            0
                        )
                        layoutActions.addView(reSend)
                    }
                }

                8 -> {
                    val freezeActions = ServiceFactory.reasons.reasons.list
                    title.text = "¿Esta seguro de congelar la orden #${items.id}?"
                    for (i in freezeActions) {
                        val v: View =
                            LayoutInflater.from(mContext)
                                .inflate(R.layout.action_item, null)
                        v.setOnClickListener {
                            presenter.actionPutFreeze(items.id!!, i.id)
                            dialog.dismiss()
                        }
                        v.action.text = i.description
                        v.action.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.freeze_icon,
                            0,
                            0,
                            0
                        )
                        layoutActions.addView(v)
                    }
                }
            }
            val cancel: View =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.action_item, null)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            cancel.action.text = "Cancelar"
            cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel,
                0,
                0,
                0
            )
            layoutActions.addView(cancel)

            dialog.setContentView(dialogView)
            dialog.show()
        })
    }

    @SuppressLint("SetTextI18n")
    fun actionsDetail(
        mContext: Context, presenter: DetailPresenter, id: Int, data: DetailResponse,
        compareArticleList: List<String>,
        articleList: MutableList<String> = mutableListOf<String>()
    ) {
        var observations: String? = null

        val dialog = BottomSheetDialog(mContext)
        val dialogView =
            LayoutInflater.from(mContext).inflate(R.layout.activity_popup, null)
        val title = dialogView.findViewById<TextView>(R.id.titleOrderId)
        val observationsView = dialogView.findViewById<EditText>(R.id.editObservations)
        val layoutActions =
            dialogView.findViewById<LinearLayout>(R.id.listActions)
        when (id) {

            3 -> {
                presenter.actionTake()
            }
            4 -> {
                var canSave=false
                for (i in articleList){
                    if(i.toInt()>=1){
                        canSave=true
                    }
                }
                if (!canSave) {
                    title.text = "Debe Pickear primero."
                } else {
                    title.text = "¿Esta seguro de guardar la orden #${data.order.id}?"
                    observationsView.visibility = View.VISIBLE
                    val v: View =
                        LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        observations = observationsView.text.toString()
                        observationsView.text = null
                        presenter.actionPick(
                            data,
                            Detail.adjustvalue,
                            compareArticleList,
                            articleList,
                            observations
                        )
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"

                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
            }
            5 -> {

                title.text = "¿Esta seguro de liberar la orden #${data.order.id}?"

                // dialogView.findViewById<EditText>(R.id.editObservations).visibility = View.VISIBLE
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    observations =
                        dialogView.findViewById<EditText>(R.id.editObservations).text.toString()
                    dialogView.findViewById<EditText>(R.id.editObservations).text = null
                    presenter.actionRelease(observations)
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)

            }
            6 -> {

                val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
                val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

                if (!BuildConfig.DEBUG) {
                    emailEdit!!.visibility = View.VISIBLE
                    phoneEdit!!.visibility = View.VISIBLE
                }
                title.text =
                    "¿Esta seguro de entregar la orden #${data.order.id} a un domiciliario?"
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    if (!BuildConfig.DEBUG) {
                        presenter.actionPutDeliverCourier(
                            emailEdit!!.text.toString(),
                            phoneEdit.text.toString()
                        )
                    } else {
                        presenter.actionPutDeliverCourier()
                    }
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)

            }
            7 -> {
                showDialogCode(
                    mContext,
                    dialogView,
                    title,
                    dialog,
                    layoutActions,
                    data,
                    presenter,
                    false
                )
            }
            8 -> {
                val freezeActions = ServiceFactory.reasons.reasons.list
                title.text = "¿Esta seguro de congelar la orden #${data.order.id}?"
                for (i in freezeActions) {
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        presenter.actionPutFreeze(i.id)
                        dialog.dismiss()
                    }
                    v.action.text = i.description
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.freeze_icon,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
            }
        }
        if (id != 3) {
            val cancel: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            cancel.action.text = "Cancelar"
            cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel,
                0,
                0,
                0
            )
            layoutActions.addView(cancel)

            dialog.setContentView(dialogView)
            dialog.show()
        }
    }

    fun showDialogCode(
        mContext: Context,
        dialogView: View,
        title: TextView,
        dialog: BottomSheetDialog,
        layoutActions: LinearLayout,
        data: DetailResponse,
        presenter: DetailPresenter,
        show: Boolean
    ) {

        val activity = mContext as Activity
        activity.runOnUiThread(Runnable {
            title.text = "¿Esta seguro de entregar la orden #${data.order.id} al Cliente?"
            val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
            val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
            val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
            codeEdit!!.visibility = View.VISIBLE

            if (!BuildConfig.DEBUG) {
                emailEdit!!.visibility = View.VISIBLE
                phoneEdit!!.visibility = View.VISIBLE
            }
            val v: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            v.setOnClickListener {
                presenter.actionPutDeliverCustomer(
                    codeEdit.text.toString()
                ) {

                    showDialogCode(
                        mContext,
                        dialogView,
                        title,
                        dialog,
                        layoutActions,
                        data,
                        presenter,
                        true
                    )
                }
                dialog.dismiss()
            }
            v.action.text = "Aceptar"
            v.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.yes_action,
                0,
                0,
                0
            )
            layoutActions.addView(v)
            if (!BuildConfig.DEBUG) {
                val reSend: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                reSend.setOnClickListener {
                    presenter.actionPutSendConfirmation(
                        emailEdit!!.text.toString(),
                        phoneEdit!!.text.toString()
                    )
                    // dialog.dismiss()
                }
                reSend.action.text = "Reenviar codigo"
                reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.message,
                    0,
                    0,
                    0
                )
                layoutActions.addView(reSend)
            }
            if (show) {
                val dialog = BottomSheetDialog(mContext)
                val dialogView =
                    LayoutInflater.from(mContext).inflate(R.layout.activity_popup, null)
                val title = dialogView.findViewById<TextView>(R.id.titleOrderId)
                val observationsView = dialogView.findViewById<EditText>(R.id.editObservations)
                val layoutActions =
                    dialogView.findViewById<LinearLayout>(R.id.listActions)
                title.text = "¿Esta seguro de entregar la orden #${data.order.id} al Cliente?"
                val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
                val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
                val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
                codeEdit!!.visibility = View.VISIBLE

                if (!BuildConfig.DEBUG) {
                    emailEdit!!.visibility = View.VISIBLE
                    phoneEdit!!.visibility = View.VISIBLE
                }
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    presenter.actionPutDeliverCustomer(
                        codeEdit.text.toString()
                    ) {

                        showDialogCode(
                            mContext,
                            dialogView,
                            title,
                            dialog,
                            layoutActions,
                            data,
                            presenter,
                            true
                        )
                    }
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)
                if (!BuildConfig.DEBUG) {
                    val reSend: View =
                        LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                    reSend.setOnClickListener {
                        presenter.actionPutSendConfirmation(
                            emailEdit!!.text.toString(),
                            phoneEdit!!.text.toString()
                        )
                        // dialog.dismiss()
                    }
                    reSend.action.text = "Reenviar codigo"
                    reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.message,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(reSend)
                }
                val cancel: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                cancel.action.text = "Cancelar"
                cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.cancel,
                    0,
                    0,
                    0
                )
                layoutActions.addView(cancel)

                dialog.setContentView(dialogView)
                dialog.show()
            }

        })
    }

}
/*
class ButtonDialogActions {
    fun actionsSection(
        mContext: Context,
        presenter: SectionPresenter,
        layoutActions: LinearLayout,
        dialog: BottomSheetDialog,
        items: OrdersList,
        actions: RealmList<Int>?,
        verDetalle: (OrdersList) -> Unit
    ) {
        for (id in actions!!) {
            val v: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            v.setOnClickListener {
                if (id == 2 || id == 4) {//Ver detalle
                    verDetalle(items)
                } else {
                    secondDialog(mContext, items, id, presenter)
                }
                dialog.dismiss()
            }

            v.action.text =
                ServiceFactory.data.actions!!.firstOrNull { it.id == id }?.name

            when (id) {
                4 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.save_icon,
                        0,
                        0,
                        0
                    )
                }
                5 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.release,
                        0,
                        0,
                        0
                    )
                }
                6 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.car,
                        0,
                        0,
                        0
                    )
                }
                7 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.persons,
                        0,
                        0,
                        0
                    )
                }
                8 -> {
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.freeze_icon,
                        0,
                        0,
                        0
                    )
                }
            }
            layoutActions.addView(v)
        }
    }

    private fun secondDialog(
        mContext: Context,
        items: OrdersList,
        id: Int,
        presenter: SectionPresenter
    ) {
        val activity = mContext as Activity
        activity.runOnUiThread(Runnable {
            val dialog = BottomSheetDialog(mContext)
            val dialogView =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.activity_popup, null)
            val title =
                dialogView.findViewById<TextView>(R.id.titleOrderId)
            val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
            val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
            val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
            val observationsView =
                dialogView.findViewById<EditText>(R.id.editObservations)
            val layoutActions =
                dialogView.findViewById<LinearLayout>(R.id.listActions)
            when (id) {
                5 -> {
                    title.text = "¿Esta seguro de liberar la orden #${items.id}?"
                    // observationsView.visibility = View.VISIBLE
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        var observations = observationsView.text.toString()
                        presenter.actionRelease(items.id!!, observations)
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
                6 -> {
                    title.text =
                        "¿Esta seguro de entregar la orden #${items.id} a un domiciliario?"
                    if (!!BuildConfig.DEBUG) {
                        emailEdit!!.visibility = View.VISIBLE
                        phoneEdit!!.visibility = View.VISIBLE
                    }
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        if (!!BuildConfig.DEBUG) {
                            presenter.actionPutDeliverCourier(
                                items.id!!,
                                emailEdit.text.toString(),
                                phoneEdit.text.toString()
                            )
                        } else {
                            presenter.actionPutDeliverCourier(
                                items.id!!
                            )
                        }
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
                7 -> {
                    title.text = "¿Esta seguro de entregar la orden #${items.id} al cliente?"
                    codeEdit!!.visibility = View.VISIBLE
                    if (!!BuildConfig.DEBUG) {
                        emailEdit!!.visibility = View.VISIBLE
                        phoneEdit!!.visibility = View.VISIBLE

                    }
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        presenter.actionPutConfirmDelivery(
                            items.id!!,
                            codeEdit.text.toString(),
                            { secondDialog(mContext, items, 7, presenter) }
                        )
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)

                    if (!!BuildConfig.DEBUG) {
                        val reSend: View =
                            LayoutInflater.from(mContext)
                                .inflate(R.layout.action_item, null)
                        reSend.setOnClickListener {
                            presenter.actionPutSendConfirmation(
                                items.id!!,
                                emailEdit!!.text.toString(),
                                phoneEdit!!.text.toString()
                            )
                            // dialog.dismiss()
                        }
                        reSend.action.text = "Reenviar codigo"
                        reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.message,
                            0,
                            0,
                            0
                        )
                        layoutActions.addView(reSend)
                    }
                }

                8 -> {
                    val freezeActions = ServiceFactory.reasons.reasons.list
                    title.text = "¿Esta seguro de congelar la orden #${items.id}?"
                    for (i in freezeActions) {
                        val v: View =
                            LayoutInflater.from(mContext)
                                .inflate(R.layout.action_item, null)
                        v.setOnClickListener {
                            presenter.actionPutFreeze(items.id!!, i.id)
                            dialog.dismiss()
                        }
                        v.action.text = i.description
                        v.action.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.freeze_icon,
                            0,
                            0,
                            0
                        )
                        layoutActions.addView(v)
                    }
                }
            }
            val cancel: View =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.action_item, null)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            cancel.action.text = "Cancelar"
            cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel,
                0,
                0,
                0
            )
            layoutActions.addView(cancel)

            dialog.setContentView(dialogView)
            dialog.show()
        })
    }

    @SuppressLint("SetTextI18n")
    fun actionsDetail(
        mContext: Context, presenter: DetailPresenter, id: Int, data: DetailResponse,
        newQuantities: List<MutableList<String>>,
        originalQuantities: MutableList<MutableList<String>> = mutableListOf<MutableList<String>>()
    ) {
        var observations: String? = null

        val dialog = BottomSheetDialog(mContext)
        val dialogView =
            LayoutInflater.from(mContext).inflate(R.layout.activity_popup, null)
        val title = dialogView.findViewById<TextView>(R.id.titleOrderId)
        val observationsView = dialogView.findViewById<EditText>(R.id.editObservations)
        val layoutActions =
            dialogView.findViewById<LinearLayout>(R.id.listActions)
        when (id) {

            3 -> {
                presenter.actionTake()
            }
            4 -> {
                var canSave=false
                for (categoryArticlesCantList in originalQuantities){
                    for (i in categoryArticlesCantList){
                        if(i.toInt()>=1){
                            canSave=true
                        }
                    }
                }
                if (!canSave) {
                    title.text = "Debe Pickear primero."
                } else {
                    title.text = "¿Esta seguro de guardar la orden #${data.id}?"
                    observationsView.visibility = View.VISIBLE
                    val v: View =
                        LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        observations = observationsView.text.toString()
                        observationsView.text = null
                        presenter.actionPick(
                            data,
                            Detail.adjustvalue,
                            newQuantities,
                            originalQuantities,
                            observations
                        )
                        dialog.dismiss()
                    }
                    v.action.text = "Aceptar"

                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.yes_action,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
            }
            5 -> {

                title.text = "¿Esta seguro de liberar la orden #${data.id}?"

                // dialogView.findViewById<EditText>(R.id.editObservations).visibility = View.VISIBLE
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    observations =
                        dialogView.findViewById<EditText>(R.id.editObservations).text.toString()
                    dialogView.findViewById<EditText>(R.id.editObservations).text = null
                    presenter.actionRelease(observations)
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)

            }
            6 -> {

                val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
                val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

                if (!!BuildConfig.DEBUG) {
                    emailEdit!!.visibility = View.VISIBLE
                    phoneEdit!!.visibility = View.VISIBLE
                }
                title.text =
                    "¿Esta seguro de entregar la orden #${data.id} a un domiciliario?"
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    if (!!BuildConfig.DEBUG) {
                        presenter.actionPutDeliverCourier(
                            emailEdit!!.text.toString(),
                            phoneEdit.text.toString()
                        )
                    } else {
                        presenter.actionPutDeliverCourier()
                    }
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)

            }
            7 -> {
                showDialogCode(
                    mContext,
                    dialogView,
                    title,
                    dialog,
                    layoutActions,
                    data,
                    presenter,
                    false
                )
            }
            8 -> {
                val freezeActions = ServiceFactory.reasons.reasons.list
                title.text = "¿Esta seguro de congelar la orden #${data.id}?"
                for (i in freezeActions) {
                    val v: View =
                        LayoutInflater.from(mContext)
                            .inflate(R.layout.action_item, null)
                    v.setOnClickListener {
                        presenter.actionPutFreeze(i.id)
                        dialog.dismiss()
                    }
                    v.action.text = i.description
                    v.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.freeze_icon,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(v)
                }
            }
        }
        if (id != 3) {
            val cancel: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            cancel.action.text = "Cancelar"
            cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.cancel,
                0,
                0,
                0
            )
            layoutActions.addView(cancel)

            dialog.setContentView(dialogView)
            dialog.show()
        }
    }

    fun showDialogCode(
        mContext: Context,
        dialogView: View,
        title: TextView,
        dialog: BottomSheetDialog,
        layoutActions: LinearLayout,
        data: DetailResponse,
        presenter: DetailPresenter,
        show: Boolean
    ) {

        val activity = mContext as Activity
        activity.runOnUiThread(Runnable {
            title.text = "¿Esta seguro de entregar la orden #${data.id} al Cliente?"
            val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
            val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
            val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
            codeEdit!!.visibility = View.VISIBLE

            if (!!BuildConfig.DEBUG) {
                emailEdit!!.visibility = View.VISIBLE
                phoneEdit!!.visibility = View.VISIBLE
            }
            val v: View =
                LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
            v.setOnClickListener {
                presenter.actionPutDeliverCustomer(
                    codeEdit.text.toString()
                ) {

                    showDialogCode(
                        mContext,
                        dialogView,
                        title,
                        dialog,
                        layoutActions,
                        data,
                        presenter,
                        true
                    )
                }
                dialog.dismiss()
            }
            v.action.text = "Aceptar"
            v.action.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.yes_action,
                0,
                0,
                0
            )
            layoutActions.addView(v)
            if (!!BuildConfig.DEBUG) {
                val reSend: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                reSend.setOnClickListener {
                    presenter.actionPutSendConfirmation(
                        emailEdit!!.text.toString(),
                        phoneEdit!!.text.toString()
                    )
                    // dialog.dismiss()
                }
                reSend.action.text = "Reenviar codigo"
                reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.message,
                    0,
                    0,
                    0
                )
                layoutActions.addView(reSend)
            }
            if (show) {
                val dialog = BottomSheetDialog(mContext)
                val dialogView =
                    LayoutInflater.from(mContext).inflate(R.layout.activity_popup, null)
                val title = dialogView.findViewById<TextView>(R.id.titleOrderId)
                val observationsView = dialogView.findViewById<EditText>(R.id.editObservations)
                val layoutActions =
                    dialogView.findViewById<LinearLayout>(R.id.listActions)
                title.text = "¿Esta seguro de entregar la orden #${data.id} al Cliente?"
                val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
                val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)
                val codeEdit = dialogView.findViewById<EditText>(R.id.editCode)
                codeEdit!!.visibility = View.VISIBLE

                if (!!BuildConfig.DEBUG) {
                    emailEdit!!.visibility = View.VISIBLE
                    phoneEdit!!.visibility = View.VISIBLE
                }
                val v: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                v.setOnClickListener {
                    presenter.actionPutDeliverCustomer(
                        codeEdit.text.toString()
                    ) {

                        showDialogCode(
                            mContext,
                            dialogView,
                            title,
                            dialog,
                            layoutActions,
                            data,
                            presenter,
                            true
                        )
                    }
                    dialog.dismiss()
                }
                v.action.text = "Aceptar"
                v.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.yes_action,
                    0,
                    0,
                    0
                )
                layoutActions.addView(v)
                if (!!BuildConfig.DEBUG) {
                    val reSend: View =
                        LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                    reSend.setOnClickListener {
                        presenter.actionPutSendConfirmation(
                            emailEdit!!.text.toString(),
                            phoneEdit!!.text.toString()
                        )
                        // dialog.dismiss()
                    }
                    reSend.action.text = "Reenviar codigo"
                    reSend.action.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.message,
                        0,
                        0,
                        0
                    )
                    layoutActions.addView(reSend)
                }
                val cancel: View =
                    LayoutInflater.from(mContext).inflate(R.layout.action_item, null)
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                cancel.action.text = "Cancelar"
                cancel.action.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.cancel,
                    0,
                    0,
                    0
                )
                layoutActions.addView(cancel)

                dialog.setContentView(dialogView)
                dialog.show()
            }

        })
    }

}
*/