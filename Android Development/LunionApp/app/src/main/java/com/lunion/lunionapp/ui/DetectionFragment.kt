package com.lunion.lunionapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lunion.lunionapp.R
import com.lunion.lunionapp.databinding.FragmentDetectionBinding
import com.lunion.lunionapp.model.PredictionModel
import com.lunion.lunionapp.model.UserModel
import com.lunion.lunionapp.utils.Constants.UPLOAD_FILE_LINK
import com.lunion.lunionapp.viewmodel.DetectionViewModel
import com.lunion.lunionapp.viewmodel.ViewModelFactory

class DetectionFragment : Fragment() {
    private lateinit var viewBinding: FragmentDetectionBinding
    private lateinit var viewModel: DetectionViewModel

    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val FILE_CHOOSER_RESULTCODE = 1
    val REQUEST_CODE_SELECT_FILE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDetectionBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel
        val factory = ViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[DetectionViewModel::class.java]

        viewBinding.wvUploadFile.loadUrl(UPLOAD_FILE_LINK)
        viewBinding.wvUploadFile.settings.allowFileAccess = true
        viewBinding.wvUploadFile.settings.javaScriptEnabled = true

        viewBinding.wvUploadFile.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                        uploadMessage = null
                    }
                    uploadMessage = filePathCallback
                    val intent = fileChooserParams.createIntent()
                    try {
                        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE)
                    } catch (e: ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(
                            requireContext(),
                            "Cannot open file chooser",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return false
                    }
                    return true
                } else {
                    return false
                }
            }
        }

        //detection n move resultDetectionActivity
        viewBinding.btnDetectLung.setOnClickListener {
            viewModel.checkEmailPatient(viewBinding.emailPatient.text.toString())
        }

        viewModel.dataUser.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d("dataku", "data: ${it.email}")
                moveResultDetection(it)
            } else {
                Toast.makeText(requireContext(), "Email doesn't exists...", Toast.LENGTH_LONG)
                    .show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_CODE_SELECT_FILE) {
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                    uploadMessage = null
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to open file uploader, please check app permissions",
                    Toast.LENGTH_LONG
                ).show()
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun moveResultDetection(data: UserModel) {
        val move = Intent(activity, ResultDetectionActivity::class.java)
        move.putExtra("DATA", data)
        startActivity(move)
    }

    companion object {
        fun newInstance(): DetectionFragment {
            val fragment = DetectionFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}