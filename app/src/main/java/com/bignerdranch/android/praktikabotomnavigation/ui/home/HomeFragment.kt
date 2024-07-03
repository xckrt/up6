package com.bignerdranch.android.praktikabotomnavigation.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContentInfo
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.praktikabotomnavigation.databinding.FragmentHomeBinding
import database.MyData
import database.MyDataBase
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var ivMyImage: ImageView
    private lateinit var imageUrl: Uri
    private val binding get() = _binding!!
    private lateinit var myDataBase: MyDataBase
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        imageUrl = createImageUri()
        ivMyImage = binding.ivMyImage
        myDataBase = MyDataBase.getInstance(requireContext())

        loadLastUser()

        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val surname = binding.editTextSurname.text.toString()
            val group = binding.editTextGroup.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && group.isNotEmpty()) {
                ivMyImage.isDrawingCacheEnabled = true
                ivMyImage.buildDrawingCache()
                val bitmap = ivMyImage.drawingCache
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageByteArray = stream.toByteArray()

                lifecycleScope.launch {
                    val existingUser = myDataBase.getDbDao().getUserById(1)
                    if (existingUser != null) {
                        val updatedUser = existingUser.copy(name = name, surname = surname, group = group, image = imageByteArray)
                        myDataBase.getDbDao().update(updatedUser)
                        Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
                    } else {
                        val newUser = MyData(id = 1, image = imageByteArray, name = name, surname = surname, group = group)
                        myDataBase.getDbDao().insert(newUser)
                        Toast.makeText(requireContext(), "Данные созданы", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            ivMyImage.setImageURI(null)
            ivMyImage.setImageURI(imageUrl)
        }

        ivMyImage.setOnClickListener {
            contract.launch(imageUrl)
        }

        val root: View = binding.root
        return root
    }

    private fun createImageUri(): Uri {
        val image = File(requireActivity().filesDir, "myPhoto.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.mypracticeapp.FileProvider",
            image)
    }
    private fun loadLastUser() {
        lifecycleScope.launch {
            val lastUser = myDataBase.getDbDao().getLastUser()
            lastUser?.let { user ->
                binding.editTextName.setText(user.name)
                binding.editTextSurname.setText(user.surname)
                binding.editTextGroup.setText(user.group)
                if (user.image.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(user.image, 0, user.image.size)
                    ivyImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
