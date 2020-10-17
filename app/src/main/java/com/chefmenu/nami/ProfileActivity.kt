package com.chefmenu.nami

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chefmenu.nami.models.user.ProfileResponse
import com.chefmenu.nami.models.user.UserResponse
import com.chefmenu.nami.presenters.ProfilePresenter
import com.chefmenu.nami.presenters.ProfileUI
import com.chefmenu.nami.singleton.VersionSingleton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.regex.Pattern

class ProfileActivity : AppCompatActivity(), ProfileUI {

    private val presenter = ProfilePresenter(this, this)
    private lateinit var profileResponse: ProfileResponse
    private lateinit var meResponse: UserResponse
    var methodTypes = mutableListOf<String>()
    private var backButton = false
    val vertionSingleton = VersionSingleton.instance

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.signout, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val actionbar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        vertionSingleton.initTimer(this)
        vertionSingleton.play()
        val intent: Intent = intent
        backButton = intent.getBooleanExtra("backButton", false)
        supportActionBar?.setDisplayHomeAsUpEnabled(backButton)
        actionbar!!.title = "Perfil"
        //actionbar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#01bd8a")))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        shimmer_view_container.visibility = View.VISIBLE
        containerProfile.visibility = View.GONE
        shimmer_view_container.startShimmer()
        presenter.getUser()
        button1.setOnClickListener {
            if (!validateForm()) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Campos invalidos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                updateProfile()
            }
        }
    }

    override fun onPause() {
        shimmer_view_container.stopShimmer()
        shimmer_view_container.visibility = View.GONE
        containerProfile.visibility = View.VISIBLE
        vertionSingleton.stop()
        super.onPause()
    }

    override fun onResume() {
        vertionSingleton.play()
        super.onResume()
        vertionSingleton.play()
    }

    private fun updateProfile() {
        presenter.actionUpdateProfile(
            name.text.toString(),
            lastName.text.toString(),
            profileResponse.typePersons?.firstOrNull { it.name == identificationType.selectedItem }?.value,
            pickerid.text.toString(),
            phone.text.toString(),
            email.text.toString(),
            entitys.selectedItem.toString(),
            profileResponse.typesAccounts?.firstOrNull { it.name == accountTypes.selectedItem }?.id.toString(),
            accountNum.text.toString()
        )
    }


    override fun onBackPressed() {
        if (!validateForm()) {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "Campos invalidos",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (
            meResponse.name != null &&
            meResponse.lastname != null &&
            meResponse.typeIdentification != null &&
            meResponse.identification != null &&
            meResponse.phone != null &&
            meResponse.email != null &&
            meResponse.entity != null &&
            meResponse.typeAccount != null &&
            meResponse.account != null &&
            meResponse.name == name.text.toString() &&
            meResponse.lastname == lastName.text.toString() &&
            meResponse.typeIdentification ==
            profileResponse.typePersons?.firstOrNull { it.name == identificationType.selectedItem }?.value &&
            meResponse.identification == pickerid.text.toString() &&
            meResponse.phone == phone.text.toString() &&
            meResponse.email == email.text.toString() &&
            meResponse.entity == entitys.selectedItem.toString() &&
            meResponse.typeAccount == profileResponse.typesAccounts?.firstOrNull { it.name == accountTypes.selectedItem }?.id.toString() &&
            meResponse.account == accountNum.text.toString()
        ) {
            finish()

        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val dialog: AlertDialog = builder.setTitle("Desea salir sin guardar cambios?")
                .setMessage("Recuerde que todos los datos son obligatorios")
                .setPositiveButton("Guardar") { _, _ ->
                    //throw RuntimeException("Forzando primer error")
                    updateProfile()
                }
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.secondaryColor))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.utilColor))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.mysignoutbutton) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Desea cerrar sesión?").setPositiveButton(
                "Salir"
            ) { _, _ -> presenter.actionLogOut() }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create().show()


        }
        Log.d("item ID : ", "onOptionsItemSelected Item ID$id")
        return if (id == android.R.id.home) {
            super.onBackPressed()
            false
        } else {
            false
        }
    }

    /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if (item.itemId == android.R.id.home) {
             Log.i("SI me tocaron", "HOMEE")
             //setRefreshInResult()
             // finish()
             super.onBackPressed()
             return true
             //setRefreshInResult()
         }
         return super.onOptionsItemSelected(item)
     }*/
    override fun showProfile(data: UserResponse, payData: ProfileResponse) {
        runOnUiThread {
            profileResponse = payData
            meResponse = data
            Log.i("el servicio", payData.toString())
            var typePersonsName = mutableListOf<String>()
            typePersonsName.add("Seleccione")
            for (i in payData.typePersons!!) {
                typePersonsName.add(i.name!!)
            }
            identificationType.adapter = ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                typePersonsName
            )
            var entities = mutableListOf<String>()
            entities.add("Seleccione")
            for (i in payData.entities!!) {
                entities.add(i.name!!)
            }
            entitys.adapter = ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                entities
            )
            entitys.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //Toast.makeText(this, "Seleccione algun dia", Toast.LENGTH_SHORT).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        showAccountTypes(payData, position - 1)
                    } else {
                        layout_accountTypes.visibility = View.GONE
                        layout_accountNum.visibility = View.GONE
                    }
                }

            }

            containerProfile?.visibility = View.VISIBLE
            button1?.visibility = View.VISIBLE
            progressContainer?.visibility = View.GONE
            identificationType.setSelection(payData.typePersons.indexOf(payData.typePersons.firstOrNull { it.value == data.typeIdentification }) + 1)
            entitys.setSelection(payData.entities.indexOf(payData.entities.firstOrNull { it.name == data.entity }) + 1)

            name.setText(data.name)
            lastName.setText(data.lastname)
            email.setText(data.email)
            phone.setText(data.phone)
            pickerid.setText(data.identification)
            accountNum.setText(data.account)

            shimmer_view_container.visibility = View.GONE
            containerProfile.visibility = View.VISIBLE
            shimmer_view_container.stopShimmer()
        }
    }

    fun showAccountTypes(payData: ProfileResponse, position: Int) {
        methodTypes.clear()
        methodTypes.add("Seleccione")
        for (i in payData.entities!![position].typesAccounts!!) {
            methodTypes.add(payData.typesAccounts!!.firstOrNull { it.id == i }!!.name!!)
        }
        accountTypes.adapter = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            methodTypes
        )
        accountTypes.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Toast.makeText(this, "Seleccione algun dia", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    layout_accountNum.visibility = View.VISIBLE
                } else {
                    layout_accountNum.visibility = View.GONE
                }
            }

        }
        layout_accountTypes.visibility = View.VISIBLE


        accountTypes.setSelection(methodTypes.indexOf(profileResponse.typesAccounts?.firstOrNull { it.id == meResponse.typeAccount?.toInt() }?.name))
    }


    override fun showSuccess(message: String) {
        runOnUiThread {
            if (!backButton) {
                finish()
            } else {
                finish()
            }
        }
    }

    override fun showError(error: String) {
        runOnUiThread {
            containerProfile?.visibility = View.VISIBLE
            button1?.visibility = View.VISIBLE
            progressContainer?.visibility = View.GONE
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun showLoad() {

        runOnUiThread {
            containerProfile?.visibility = View.GONE
            button1?.visibility = View.GONE
            progressContainer?.visibility = View.VISIBLE
        }
    }

    override fun exit() {
        finish()
    }

    private fun validateForm(): Boolean {
        if (!validateFormField(name, layout_name, "nombre")) {
            return false
        } else if (!validateFormField(lastName, layout_lastname, "apellido")) {
            return false
        } else if (!validateSpinner(
                identificationType,
                layout_identificationType,
                "Tipo de documento"
            )
        ) {
            return false
        } else if (!validateFormField(pickerid, layout_pickerId, "documento")) {
            return false
        } else if (!validateFormField(phone, layout_phone, "teléfono")) {
            return false
        } else if (!validateFormField(email, layout_email, "correo")) {
            return false
        } else if (!validateSpinner(
                entitys,
                layout_entitys,
                "Entidad Bancaria"
            )
        ) {
            return false
        } else if (!validateSpinner(
                accountTypes,
                layout_accountTypes,
                "Tipo de cuenta"
            )
        ) {
            return false
        } else return validateFormField(accountNum, layout_accountNum, "número de cuenta")
    }

    private fun validateFormField(
        editText: EditText,
        layout_input: TextInputLayout,
        field: String
    ): Boolean {
        if (editText.text.isNotEmpty()) {
            val regEx: String = when (field) {
                "nombre" ->
                    "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ]*$"
                "apellido" ->
                    "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ]*$"
                "documento" ->
                    "\\d{5,10}"
                "teléfono" ->
                    "\\d{10}"
                "correo" ->
                    ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
                "número de cuenta" ->
                    "\\d{6,11}"
                else -> "false"
            }
            return if (Pattern.compile(regEx).matcher(editText.text.toString()).matches()) {
                layout_input.isErrorEnabled = false
                true
            } else {
                layout_input.error = "El $field es inválido"
                layout_input.isErrorEnabled = true
                false
            }
        } else {
            layout_input.error = "El $field es inválido"
            layout_input.isErrorEnabled = true
            return false
        }
    }

    private fun validateSpinner(
        spinner: Spinner,
        layout_input: TextInputLayout,
        field: String
    ): Boolean {
        return if (spinner.selectedItem != "Seleccione") {
            layout_input.isErrorEnabled = false
            true
        } else {
            layout_input.error = "El $field es inválido"
            layout_input.isErrorEnabled = true
            false
        }
    }
}