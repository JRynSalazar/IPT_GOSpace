package com.example.gospace_ipt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityAdminAccntSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AdminAccntSettings : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAccntSettingsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAccntSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser

        binding.backBTN.setOnClickListener{
            onBackPressed()
        }


// -----------------Loading-----------
        val progressBar = findViewById<ImageView>(R.id.animatedProgressBar)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        //--------------update Name and Gender-----------------

        binding.saveData.setOnClickListener{
            val newName = binding.name.text.toString()
            val newGender = binding.gender.selectedItem.toString()

            if (newName.isNotEmpty() || newGender.isNotEmpty()) {
                showProgressBar()
                val userID = currentUser?.uid
                if (userID != null) {
                    val userRef = database.child("users").child(userID)
                    val updatedUserData = mapOf(
                        "name" to newName,
                        "gender" to newGender
                    )

                    userRef.updateChildren(updatedUserData).addOnCompleteListener { task ->
                        hideProgressBar()
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            binding.name.setText("")
                            binding.gender.setSelection(0)
                        } else {
                            Toast.makeText(this, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            } else {
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show()
            }
        }





        //-------------- Update password-------------------
        binding.updatePass.setOnClickListener {
            val oldPassword = binding.oldPass.text.toString().trim()
            val newPassword = binding.newPass.text.toString().trim()
            val confirmNewPassword = binding.confirmNewPass.text.toString().trim()

            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty()) {
                if (newPassword == confirmNewPassword) {
                    showProgressBar()
                    updatePassword(oldPassword, newPassword)
                } else {
                    Toast.makeText(this, "New passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }

        //-------------Delete account---------------
        binding.deleteAccnt1.setOnClickListener {
            delete()
        }
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        val email = user?.email

        if (email != null) {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            updatePasswordInDatabase(newPassword)
                            binding.oldPass.setText("")
                            binding.newPass.setText("")
                            binding.confirmNewPass.setText("")
                        } else {
                            Toast.makeText(this, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                        hideProgressBar()
                    }
                } else {
                    Toast.makeText(this, "Authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            }
        } else {
            Toast.makeText(this, "Email is null. Cannot reauthenticate.", Toast.LENGTH_SHORT).show()
            hideProgressBar()
        }
    }
//-------------------update pass in database--------------------
    private fun updatePasswordInDatabase(newPassword: String) {
        val userId = currentUser?.uid
        if (userId != null) {
            val userRef = database.child("users").child(userId)
            userRef.child("password").setValue(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password updated successfully in the database!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update password in the database: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
        }
    }
//-------------------delete function notification--------------------------
    private fun delete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ -> deleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }
//-------------------delete function---------------
    private fun deleteAccount() {
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account deleted successfully!", Toast.LENGTH_SHORT).show()
                navigateToChooseUser()
            } else {
                val error = task.exception?.localizedMessage ?: "Account deletion failed."
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun navigateToChooseUser() {
        val intent = Intent(this, ChooseUser::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
}
