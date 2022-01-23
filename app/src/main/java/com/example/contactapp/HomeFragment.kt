package com.example.contactapp

import android.Manifest
import android.Manifest.*
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contactapp.adapters.RvAdapter
import com.example.contactapp.databinding.FragmentHomeBinding
import com.example.contactapp.models.User
import com.github.florent37.runtimepermission.kotlin.askPermission
import java.security.acl.Permission


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var users: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }
    lateinit var bind:FragmentHomeBinding
    lateinit var rvAdapter: RvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        bind = FragmentHomeBinding.bind(view)

        users = ArrayList()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getContactList()
        } else {
            myMetod()
        }
        rvAdapter = RvAdapter(users, object : RvAdapter.MyInterface {
            override fun smsClick(user: User) {
                val bundle = Bundle()
                bundle.putSerializable("user", user)
                findNavController().navigate(R.id.smsFragment, bundle)
            }

            override fun callClick(user: User) {
                val number: String = user.number
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$number")
                startActivity(callIntent)
            }
        })
        bind.rv.adapter = rvAdapter
        rvAdapter.notifyDataSetChanged()
        return view
    }

    private fun myMetod() {
        askPermission(
            permission.READ_CONTACTS,
            permission.CALL_PHONE,
            permission.SEND_SMS,
            permission.RECEIVE_SMS,
            permission.WRITE_CONTACTS
        ) {
            getContactList()
            bind.rv.adapter = rvAdapter
        }.onDeclined { e ->
            if (e.hasDenied()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain()
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show()
            }
            if (e.hasForeverDenied()) {
                e.goToSettings();
            }
        }
    }

    private fun getContactList() {
        val cr: ContentResolver = requireActivity().contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        Log.i("tag", "Name: $name")
                        Log.i("tag", "Phone Number: $phoneNo")
                        users.add(User(name, phoneNo))
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
    }
}