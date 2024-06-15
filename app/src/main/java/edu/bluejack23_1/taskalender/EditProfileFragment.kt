package edu.bluejack23_1.taskalender

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import edu.bluejack23_1.taskalender.view_model.profile.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    private var dobTextInputLayout: TextInputLayout? = null
    private lateinit var backLink: ImageView
    private lateinit var emailTxt: TextInputEditText
    private lateinit var usernameTxt: TextInputEditText
    private lateinit var phoneTxt: TextInputEditText
    private lateinit var dobTxt: TextView
    private lateinit var editProfileBtn: CardView

    private lateinit var oldPasswordLayout: TextInputLayout
    private lateinit var oldPassword: TextInputEditText
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var newPassword: TextInputEditText
    private lateinit var confirmationPasswordLayout: TextInputLayout
    private lateinit var confirmationPassword: TextInputEditText
    private lateinit var changePasswordBtn: CardView

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
        val root = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        val uid = sharedPreferences.getString(LoginActivity.UID, null)
        dobTextInputLayout = root.findViewById(R.id.dobContainer)
        backLink = root.findViewById(R.id.backLink)
        emailTxt = root.findViewById(R.id.email)
        usernameTxt = root.findViewById(R.id.username)
        phoneTxt = root.findViewById(R.id.phone)
        dobTxt = root.findViewById(R.id.dob)
        editProfileBtn = root.findViewById(R.id.editProfileBtn)

        oldPasswordLayout = root.findViewById(R.id.oldPasswordContainer)
        oldPassword = root.findViewById(R.id.oldPassword)
        newPasswordLayout = root.findViewById(R.id.newPasswordContainer)
        newPassword = root.findViewById(R.id.newPassword)
        confirmationPasswordLayout = root.findViewById(R.id.confirmationPasswordContainer)
        confirmationPassword = root.findViewById(R.id.confirmationPassword)
        changePasswordBtn = root.findViewById(R.id.changePasswordBtn)

        profileViewModel.fetchUserInfo(uid.toString())
        profileViewModel.getUserInfoLiveData().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)

                emailTxt.setText(user.email)
                usernameTxt.setText(user.username)
                phoneTxt.setText(user.phone.substring(3))
                dobTxt.text = dateFormat.format(user.dob.toDate())
            }
        }

        dobTextInputLayout?.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                val date = formatDate.format(selectDate.time)
                dobTxt?.text = date
            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show();
        }

        backLink.setOnClickListener{
            val fragment = ProfileFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment)
            transaction.commit()
        }

        editProfileBtn.setOnClickListener{
            val newUsername = usernameTxt.text.toString()
            val newDobText = dobTxt.text.toString()
            val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            val dateFormat = inputFormat.parse(newDobText)
            val timestamp = dateFormat?.time

            profileViewModel.updateUserProfile(uid, newUsername, Timestamp(Date(timestamp ?: 0)))

            Toast.makeText(requireContext(), "Edit Profile Successful", Toast.LENGTH_SHORT).show()
            val fragment = ProfileFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment)
            transaction.commit()
        }

        changePasswordBtn.setOnClickListener{
            oldPasswordLayout.error = null
            newPasswordLayout.error = null
            confirmationPasswordLayout.error = null
            if(oldPassword.text.toString().isNotEmpty() && newPassword.text.toString().isNotEmpty() && confirmationPassword.text.toString().isNotEmpty()){
                if (newPassword.text.toString() == confirmationPassword.text.toString()) {
                    profileViewModel.updatePassword(
                        oldPassword.text.toString(),
                        newPassword.text.toString(),
                        onSuccess = {
                            Toast.makeText(requireContext(), "Password Updated", Toast.LENGTH_SHORT).show()
                            val fragment = ProfileFragment()
                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame_container, fragment)
                            transaction.commit()
                        },
                        onFailure = { errorMessage ->
                            Toast.makeText(requireContext(), "Old Password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    confirmationPasswordLayout.error = "New password and confirmation password do not match."
                }
            }else if (oldPassword.text.toString().isEmpty()) {
                oldPasswordLayout.error = "Old password is required"
            } else if (newPassword.text.toString().isEmpty()) {
                newPasswordLayout.error = "New password is required"
            } else if (confirmationPassword.text.toString().isEmpty()) {
                confirmationPasswordLayout.error = "Confirmation password is required"
            }
        }

        return root
    }
}