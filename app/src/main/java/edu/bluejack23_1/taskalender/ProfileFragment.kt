package edu.bluejack23_1.taskalender

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.view_model.profile.ProfileViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var logoutBtn: CardView
    private lateinit var editLink: ImageView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView

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


        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        username = root.findViewById(R.id.usernameTxt)
        email = root.findViewById(R.id.emailTxt)
        phone = root.findViewById(R.id.phoneTxt)

        logoutBtn = root.findViewById(R.id.logoutBtn)
        editLink = root.findViewById(R.id.editLink)

        profileViewModel.fetchUserInfo(uid.toString())
        profileViewModel.getUserInfoLiveData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                username.text = user.username
                email.text = user.email
                phone.text = user.phone
            }
        }

        logoutBtn.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()

            Toast.makeText(requireContext(), "Successfully Sign Out", Toast.LENGTH_SHORT).show()
            val i = Intent(requireContext(), LoginActivity::class.java)
            startActivity(i)

        }

        editLink.setOnClickListener{
            val fragment = EditProfileFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment)
            transaction.commit()
        }

        return root;
    }





}