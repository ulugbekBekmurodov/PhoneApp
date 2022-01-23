package com.example.contactapp

import android.Manifest.permission_group.SMS
import android.R.attr
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contactapp.databinding.FragmentSmsBinding
import com.example.contactapp.models.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SmsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sms, container, false)
        val user = arguments?.getSerializable("user") as User

        val bind = FragmentSmsBinding.bind(view)

        bind.name.text = user.name
        bind.back.setOnClickListener {
            findNavController().popBackStack()
        }
        bind.phoneNumber.text = user.number
        bind.btn.setOnClickListener {
            val phoneNumber = bind.phoneNumber.text.toString()
            val smsTxt = bind.sendSms.text.toString()

            if (smsTxt.isNotEmpty()) {
                if (smsTxt.isNotEmpty())
                    sendSMS(phoneNumber, smsTxt);
                else
                    Toast.makeText(
                        requireContext(),
                        "Please enter both phone number and message.",
                        Toast.LENGTH_SHORT
                    ).show();
            }
        }
        return view
    }
    private fun sendSMS(phoneNumber: String, smsTxt: String) {
        val pi = PendingIntent.getActivity(
            requireContext(), 0,
            Intent(requireContext(), SMS::class.java), 0
        )
        val sms: SmsManager = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, smsTxt, pi, null)
        Toast.makeText(requireContext(), "Habar muvofaqiyatli yuborildi", Toast.LENGTH_SHORT).show()
    }
}